package campusCommute;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * DriverGUI
 * Fully Backend Synced.
 *
 * --- CORRECTED VERSION ---
 * - Uses correct JList for requests
 * - Uses efficient API endpoint
 * - Correctly accepts selected request
 */
public class DriverGUI extends JFrame {

    // UI Components
    private DefaultListModel<String> rideListModel;
    private JList<String> rideListUI;

    // --- FIX 1: Changed JTextArea to a JList for requests ---
    private DefaultListModel<RideRequest> requestListModel;
    private JList<RideRequest> requestListUI;

    private JTable historyTable;
    private DefaultTableModel historyModel;

    // Backend data
    private List<Ride> driverRides = new ArrayList<>();
    private List<RideRequest> currentRequests = new ArrayList<>(); // Kept for logic

    private final Gson gson = new Gson();

    public DriverGUI() {

        // --- FIX 2: Added the login check ---
        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to view the Driver Panel.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            dispose(); // Close this broken window
            return;    // Stop running the rest of the constructor
        }
        // --- END OF FIX ---

        setTitle("Driver Panel");
        setSize(800, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        setContentPane(tabs);

        // -------------------------------------------------------
        // TAB 1: DRIVER RIDES + REQUESTS
        // -------------------------------------------------------
        JPanel ridePanel = new JPanel(new BorderLayout(10, 10));
        tabs.addTab("My Rides", ridePanel);

        rideListModel = new DefaultListModel<>();
        rideListUI = new JList<>(rideListModel);
        rideListUI.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane rideScroll = new JScrollPane(rideListUI);
        rideScroll.setBorder(BorderFactory.createTitledBorder("Your Rides"));

        ridePanel.add(rideScroll, BorderLayout.WEST);

        // --- FIX 3: Initialized the new JList for requests ---
        requestListModel = new DefaultListModel<>();
        requestListUI = new JList<>(requestListModel);
        requestListUI.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Use a custom renderer to show the passenger's name
        requestListUI.setCellRenderer(new RequestRenderer());

        JScrollPane requestScroll = new JScrollPane(requestListUI);
        requestScroll.setBorder(BorderFactory.createTitledBorder("Passenger Requests"));
        // --- END OF FIX ---

        ridePanel.add(requestScroll, BorderLayout.CENTER);

        JButton acceptButton = new JButton("Accept Request");
        ridePanel.add(acceptButton, BorderLayout.SOUTH);

        rideListUI.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRequestsForSelectedRide();
            }
        });

        acceptButton.addActionListener(e -> acceptSelectedRequest());

        // ... (rest of the constructor for History tab) ...
        
        // -------------------------------------------------------
        // TAB 2: RIDE HISTORY
        // -------------------------------------------------------
        JPanel historyPanel = new JPanel(new BorderLayout());
        tabs.addTab("Ride History", historyPanel);

        String[] cols = {"Date", "From", "To", "Price"};
        historyModel = new DefaultTableModel(cols, 0);
        historyTable = new JTable(historyModel);
        historyTable.setRowHeight(22);

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyPanel.add(historyScroll, BorderLayout.CENTER);

        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) {
                loadDriverHistory();
            }
        });

        // Load data on startup
        loadDriverRides();

        setVisible(true);
    }

    // ========================================================
    // LOAD DRIVER RIDES FROM BACKEND (FIXED)
    // ========================================================
    private void loadDriverRides() {
        try {
            // 1. Get the ID (this is now safe because of the constructor check)
            long driverId = CurrentUser.loggedInUser.getID();

            // 2. Use the correct, specific API endpoint
            URL url = new URL("http://localhost:8080/api/rides/driver/" + driverId);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            // 3. The JSON now *only* contains this driver's rides
            Ride[] rides = gson.fromJson(json, Ride[].class);
            
            driverRides.clear();
            rideListModel.clear();

            // 4. No more "if" filter needed! Just add all of them.
            for (Ride r : rides) {
                driverRides.add(r);
                rideListModel.addElement(r.toString());
            }

        } catch (Exception e) {
            // This will still catch server errors, which is good.
            JOptionPane.showMessageDialog(this, "Error loading rides.");
            e.printStackTrace(); // Helps you debug
        }
    }

    // ========================================================
    // LOAD REQUESTS FOR SELECTED RIDE (FIXED)
    // ========================================================
    private void loadRequestsForSelectedRide() {
        // Clear the UI lists first
        requestListModel.clear();
        currentRequests.clear();

        int index = rideListUI.getSelectedIndex();
        if (index == -1) {
            return; // Nothing is selected
        }

        // 1. Get the ride object you ALREADY downloaded
        Ride selectedRide = driverRides.get(index);

        // 2. Get the list of requests DIRECTLY from that object.
        //    (No new network call is needed!)
        List<RideRequest> requests = selectedRide.getRequests();

        // 3. Populate the UI from the list we just retrieved
        if (requests != null && !requests.isEmpty()) {
            for (RideRequest req : requests) {
                
                // Only show pending requests that can be accepted
                if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                    requestListModel.addElement(req);
                    currentRequests.add(req);
                }
            }
        }

        // 4. If no *pending* requests were found, show a message
        if (requestListModel.isEmpty()) {
            RideRequest placeholder = new RideRequest(0L, "No pending requests.");
            requestListModel.addElement(placeholder);
        }
    }

    // ========================================================
    // ACCEPT REQUEST (FIXED)
    // ========================================================
    private void acceptSelectedRequest() {

        int rideIndex = rideListUI.getSelectedIndex();
        // --- FIX: Get the selected request from the new JList ---
        int requestIndex = requestListUI.getSelectedIndex();

        if (rideIndex == -1 || requestIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ride AND a request to accept.");
            return;
        }

        Ride ride = driverRides.get(rideIndex);
        // --- FIX: Get the actual selected request object ---
        RideRequest req = requestListUI.getSelectedValue();

        // Check if it's the "No pending requests" placeholder
        if (req.getPassengerId() == 0L) return;

        long driverId = CurrentUser.loggedInUser.getID();
        long passengerId = req.getPassengerId();
        long requestId = req.getId();

        try {
            // Build the URL with all required parameters
            String urlString = String.format("http://localhost:8080/api/ride/%d/accept?driverId=%d&passengerId=%d&requestId=%d",
                    ride.getId(),
                    driverId,
                    passengerId,
                    requestId);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            // Check response code to confirm success
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                 JOptionPane.showMessageDialog(this, "Request accepted! Ride marked as COMPLETED!");
                 // Refresh the UI
                 loadDriverRides();
                 requestListModel.clear();
                 currentRequests.clear();
            } else {
                 JOptionPane.showMessageDialog(this, "Failed to accept request. Server responded with: " + responseCode);
            }
            conn.getInputStream().close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to accept request: " + e.getMessage());
        }
    }

    // ========================================================
    // LOAD DRIVER RIDE HISTORY
    // ========================================================
    private void loadDriverHistory() {
        try {
            long id = CurrentUser.loggedInUser.getID();
            URL url = new URL("http://localhost:8080/api/history/driver/" + id);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            Ride[] history = gson.fromJson(json, Ride[].class);

            historyModel.setRowCount(0);
            if (history == null || history.length == 0) {
                historyModel.addRow(new Object[]{"No completed rides", "", "", ""});
                return;
            }

            for (Ride r : history) {
                historyModel.addRow(new Object[]{
                        r.getDate(),
                        r.getStartLocation(),
                        r.getDestination(),
                        "$" + r.getPrice()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading history.");
        }
    }

    // --- FIX: Added an inner class to render the request list ---
    /**
     * Custom ListCellRenderer to display RideRequest objects in a JList.
     * It shows the passenger's name and status.
     */
    class RequestRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            
            // Let the default renderer handle colors, selection, etc.
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RideRequest) {
                RideRequest req = (RideRequest) value;
                // Don't show ID for the placeholder
                if (req.getPassengerId() == 0L) {
                    setText(req.getPassengerName());
                } else {
                    setText(String.format("Passenger: %s (ID: %d)", req.getPassengerName(), req.getPassengerId()));
                }
            }
            return c;
        }
    }
}