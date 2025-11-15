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
 */
public class DriverGUI extends JFrame {

    private DefaultListModel<String> rideListModel;
    private JList<String> rideListUI;

    private JTextArea requestArea;

    private JTable historyTable;
    private DefaultTableModel historyModel;

    private List<Ride> driverRides = new ArrayList<>();
    private List<RideRequest> currentRequests = new ArrayList<>();

    private final Gson gson = new Gson();

    public DriverGUI() {
    	
    	
    	if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to view the Driver Panel.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            dispose(); // Close this broken window
            return;    // Stop running the rest of the constructor
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

        requestArea = new JTextArea();
        requestArea.setEditable(false);
        requestArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane requestScroll = new JScrollPane(requestArea);
        requestScroll.setBorder(BorderFactory.createTitledBorder("Passenger Requests"));

        ridePanel.add(requestScroll, BorderLayout.CENTER);

        JButton acceptButton = new JButton("Accept Request");
        ridePanel.add(acceptButton, BorderLayout.SOUTH);

        rideListUI.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRequestsForSelectedRide();
            }
        });

        acceptButton.addActionListener(e -> acceptSelectedRequest());

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

        loadDriverRides();

        setVisible(true);
    }

    // ========================================================
    // LOAD DRIVER RIDES FROM BACKEND
    // ========================================================
    private void loadDriverRides() {
        try {
            // 1. Get the ID (this is now safe because of the constructor check)
            long driverId = CurrentUser.loggedInUser.getID();

            // 2. Use the correct, specific API endpoint from your RideController
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
    }    // ========================================================
    // LOAD REQUESTS FOR SELECTED RIDE
    // ========================================================
    private void loadRequestsForSelectedRide() {
        int index = rideListUI.getSelectedIndex();
        if (index == -1) return;

        Ride selectedRide = driverRides.get(index);

        try {
            URL url = new URL("http://localhost:8080/api/rides/" + selectedRide.getId() + "/requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.lines().collect(Collectors.joining());
            reader.close();

            RideRequest[] reqs = gson.fromJson(json, RideRequest[].class);
            currentRequests = List.of(reqs);

            StringBuilder sb = new StringBuilder();
            if (reqs.length == 0) {
                sb.append("No requests yet.");
            } else {
                for (int i = 0; i < reqs.length; i++) {
                    sb.append((i + 1)).append(". ").append(reqs[i].getPassengerName()).append("\n");
                }
            }

            requestArea.setText(sb.toString());

        } catch (Exception e) {
            requestArea.setText("Error loading requests.");
        }
    }

    // ========================================================
    // ACCEPT REQUEST
    // ========================================================
    private void acceptSelectedRequest() {

        int rideIndex = rideListUI.getSelectedIndex();
        if (rideIndex == -1 || currentRequests.isEmpty()) return;

        Ride ride = driverRides.get(rideIndex);
        RideRequest req = currentRequests.get(0);

        long driverId = CurrentUser.loggedInUser.getID();
        long passengerId = req.getPassengerId();
        long requestId = req.getId();

        try {
            URL url = new URL("http://localhost:8080/api/ride/" +
                    ride.getId() + "/accept?driverId=" + driverId +
                    "&passengerId=" + passengerId +
                    "&requestId=" + requestId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.getInputStream().close();

            JOptionPane.showMessageDialog(this, "Ride marked as COMPLETED!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to accept request.");
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
            if (history.length == 0) {
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
}
