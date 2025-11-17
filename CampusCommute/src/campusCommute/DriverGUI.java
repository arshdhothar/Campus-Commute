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
 * --- NOW INCLUDES 'MY PROFILE' TAB AND 'POST RIDE' BUTTON ---
 */
public class DriverGUI extends JFrame {

    // UI Components
    private DefaultListModel<String> rideListModel;
    private JList<String> rideListUI;

    private DefaultListModel<RideRequest> requestListModel;
    private JList<RideRequest> requestListUI;

    private JTable historyTable;
    private DefaultTableModel historyModel;

    // Backend data
    private List<Ride> driverRides = new ArrayList<>();
    private List<RideRequest> currentRequests = new ArrayList<>();

    private final Gson gson = new Gson();

    public DriverGUI() {

        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to view the Driver Panel.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            dispose(); 
            return;    
        }

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

        requestListModel = new DefaultListModel<>();
        requestListUI = new JList<>(requestListModel);
        requestListUI.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestListUI.setCellRenderer(new RequestRenderer());

        JScrollPane requestScroll = new JScrollPane(requestListUI);
        requestScroll.setBorder(BorderFactory.createTitledBorder("Passenger Requests"));

        ridePanel.add(requestScroll, BorderLayout.CENTER);

        // --- MODIFIED: Created a panel to hold both buttons ---
        JPanel southButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton acceptButton = new JButton("Accept Request");
        acceptButton.addActionListener(e -> acceptSelectedRequest());
        southButtonPanel.add(acceptButton);

        // --- NEW: Added the "Post a New Ride" button ---
        JButton postRideButton = new JButton("Post a New Ride");
        postRideButton.addActionListener(e -> openCampusCommuteGUI());
        southButtonPanel.add(postRideButton);

        // Add the new button panel to the bottom
        ridePanel.add(southButtonPanel, BorderLayout.SOUTH);
        // --- END OF MODIFICATION ---

        rideListUI.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRequestsForSelectedRide();
            }
        });

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

        // -------------------------------------------------------
        // TAB 3: MY PROFILE (opens new window)
        // -------------------------------------------------------
        JPanel profilePlaceholderPanel = new JPanel();
        tabs.addTab("My Profile", profilePlaceholderPanel);
        

        // --- MODIFIED ChangeListener ---
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            
            if (selectedIndex == 1) {
                // User clicked "Ride History"
                loadDriverHistory();
            } else if (selectedIndex == 2) {
                // User clicked "My Profile"
                // Open the ViewProfileGUI as a new window
                new ViewProfileGUI(CurrentUser.loggedInUser);
                
                // Set the tab back to "My Rides" so it doesn't
                // look like they are "on" the empty profile tab.
                tabs.setSelectedIndex(0); 
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
            long driverId = CurrentUser.loggedInUser.getID();
            URL url = new URL("http://localhost:8080/api/rides/driver/" + driverId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            Ride[] rides = gson.fromJson(json, Ride[].class);
            
            driverRides.clear();
            rideListModel.clear();

            for (Ride r : rides) {
                driverRides.add(r);
                rideListModel.addElement(r.toString());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading rides.");
            e.printStackTrace(); 
        }
    }

    // ========================================================
    // LOAD REQUESTS FOR SELECTED RIDE (FIXED)
    // ========================================================
    private void loadRequestsForSelectedRide() {
        requestListModel.clear();
        currentRequests.clear();

        int index = rideListUI.getSelectedIndex();
        if (index == -1) {
            return; 
        }

        Ride selectedRide = driverRides.get(index);
        List<RideRequest> requests = selectedRide.getRequests();

        if (requests != null && !requests.isEmpty()) {
            for (RideRequest req : requests) {
                if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                    requestListModel.addElement(req);
                    currentRequests.add(req);
                }
            }
        }

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
        int requestIndex = requestListUI.getSelectedIndex();

        if (rideIndex == -1 || requestIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ride AND a request to accept.");
            return;
        }

        Ride ride = driverRides.get(rideIndex);
        RideRequest req = requestListUI.getSelectedValue();

        if (req.getPassengerId() == 0L) return;

        long driverId = CurrentUser.loggedInUser.getID();
        long passengerId = req.getPassengerId();
        long requestId = req.getId();

        try {
            String urlString = String.format("http://localhost:8080/api/ride/%d/accept?driverId=%d&passengerId=%d&requestId=%d",
                    ride.getId(),
                    driverId,
                    passengerId,
                    requestId);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                 JOptionPane.showMessageDialog(this, "Request accepted! Ride marked as COMPLETED!");
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
    
    // --- NEW HELPER METHOD ---
    /**
     * Opens the CampusCommuteGUI (ride posting) window.
     * This is safe because both GUIs have login checks.
     */
    private void openCampusCommuteGUI() {
        new CampusCommuteGUI();
    }
    // --- END OF NEW METHOD ---

    /**
     * Custom ListCellRenderer to display RideRequest objects in a JList.
     */
    class RequestRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RideRequest) {
                RideRequest req = (RideRequest) value;
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