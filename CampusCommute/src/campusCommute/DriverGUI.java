package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * DriverGUI
 * - Displays driver's active rides and incoming requests.
 * - Allows approving requests via the CORRECT backend endpoint.
 * - Includes Chat functionality.
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
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public DriverGUI() {
        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first.");
            dispose();
            return;
        }

        setTitle("Driver Dashboard - " + CurrentUser.getName());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Manage Rides & Requests
        tabbedPane.addTab("Manage Rides", createManagePanel());

        // Tab 2: Ride History
        tabbedPane.addTab("Ride History", createHistoryPanel());

        // Tab 3: My Profile (Quick Link)
        JPanel profileWrapper = new JPanel(new BorderLayout());
        JButton openProfileBtn = new JButton("Open My Full Profile");
        openProfileBtn.addActionListener(e -> new ViewProfileGUI(CurrentUser.loggedInUser).setVisible(true));
        JPanel center = new JPanel(new GridBagLayout());
        center.add(openProfileBtn);
        profileWrapper.add(center);
        tabbedPane.addTab("My Profile", profileWrapper);

        add(tabbedPane);
        
        // Initial Load
        loadDriverData();
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // LEFT: My Active Rides
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("My Active Rides"));
        
        rideListModel = new DefaultListModel<>();
        rideListUI = new JList<>(rideListModel);
        leftPanel.add(new JScrollPane(rideListUI), BorderLayout.CENTER);
        
        // --- MODIFIED: Button Panel for Post Ride + Chat ---
        JPanel leftBtnPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JButton postRideBtn = new JButton("Post New Ride");
        postRideBtn.addActionListener(e -> new CampusCommuteGUI().setVisible(true));
        
        // NEW CHAT BUTTON
        JButton chatBtn = new JButton("Ride Chat");
        chatBtn.addActionListener(e -> {
            int index = rideListUI.getSelectedIndex();
            if (index != -1) {
                Ride r = driverRides.get(index);
                new ChatGUI(r.getId()); // Opens the chat window for the selected ride
            } else {
                JOptionPane.showMessageDialog(this, "Select a ride to open chat.");
            }
        });

        leftBtnPanel.add(postRideBtn);
        leftBtnPanel.add(chatBtn);
        
        leftPanel.add(leftBtnPanel, BorderLayout.SOUTH);
        // ---------------------------------------------------

        // RIGHT: Incoming Requests
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Incoming Requests"));
        
        requestListModel = new DefaultListModel<>();
        requestListUI = new JList<>(requestListModel);
        // Custom renderer to show passenger names nicely
        requestListUI.setCellRenderer(new RequestRenderer());
        
        rightPanel.add(new JScrollPane(requestListUI), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton approveBtn = new JButton("Approve Request");
        JButton refreshBtn = new JButton("Refresh Data");
        
        approveBtn.setBackground(new Color(144, 238, 144)); // Light green
        approveBtn.setOpaque(true);
        approveBtn.setBorderPainted(false);

        approveBtn.addActionListener(e -> approveSelectedRequest());
        refreshBtn.addActionListener(e -> loadDriverData());

        btnPanel.add(approveBtn);
        btnPanel.add(refreshBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(leftPanel);
        panel.add(rightPanel);
        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Date", "Start", "Destination", "Price", "Rating"};
        historyModel = new DefaultTableModel(cols, 0);
        historyTable = new JTable(historyModel);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        
        JButton refreshHist = new JButton("Refresh History");
        refreshHist.addActionListener(e -> loadHistory());
        panel.add(refreshHist, BorderLayout.SOUTH);
        return panel;
    }

    // =================================================================
    // DATA LOADING
    // =================================================================

    private void loadDriverData() {
        loadMyRides();
        // We reload requests implicitly by checking rides, 
        // but let's clear the list first to avoid duplicates
        requestListModel.clear();
        currentRequests.clear();
        
        // Re-fetch rides to get embedded requests
        for (Ride ride : driverRides) {
             // Only show requests for PENDING or APPROVED rides (not completed)
             if (!"COMPLETED".equals(ride.getStatus()) && ride.getRequests() != null) {
                 for (RideRequest req : ride.getRequests()) {
                     if ("PENDING".equals(req.getStatus())) {
                         currentRequests.add(req);
                         requestListModel.addElement(req);
                     }
                 }
             }
        }
    }

    private void loadMyRides() {
        try {
            Long driverId = CurrentUser.getId();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/driver/" + driverId))
                    .GET().build();
            
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                Ride[] rides = gson.fromJson(resp.body(), Ride[].class);
                driverRides.clear();
                rideListModel.clear();
                
                if (rides != null) {
                    for (Ride r : rides) {
                        driverRides.add(r);
                        rideListModel.addElement(r.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        // (Same as before, fetches completed rides)
        // NOTE: Ensure RideService is available or replace with direct HTTP if needed
        Ride[] history = RideService.getInstance().getDriverHistory(CurrentUser.getId());
        historyModel.setRowCount(0);
        if (history != null) {
            for (Ride r : history) {
                String ratingStr = (r.getDriverRating() > 0) ? String.format("%.1f", r.getDriverRating()) : "-";
                historyModel.addRow(new Object[]{r.getDate(), r.getStartLocation(), r.getDestination(), "$" + r.getPrice(), ratingStr});
            }
        }
    }

    // =================================================================
    // APPROVE LOGIC (THE FIX)
    // =================================================================

    private void approveSelectedRequest() {
        RideRequest selected = requestListUI.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.");
            return;
        }

        // FIX: Use the Request ID, not Ride/Driver IDs
        Long reqId = selected.getId();

        try {
            // Correct URL: /api/requests/{id}/status
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/requests/" + reqId + "/status"))
                    .POST(HttpRequest.BodyPublishers.ofString("APPROVED"))
                    .header("Content-Type", "text/plain") 
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Request Approved!");
                // Remove from list locally so we don't have to refresh everything
                requestListModel.removeElement(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Server said: " + response.statusCode());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Custom renderer to make the request list look nice
    class RequestRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof RideRequest) {
                RideRequest req = (RideRequest) value;
                setText("Passenger: " + req.getPassengerName() + " (ID: " + req.getPassengerId() + ")");
            }
            return this;
        }
    }
}