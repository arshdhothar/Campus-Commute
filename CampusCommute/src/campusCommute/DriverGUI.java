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
 * - Displays driver's active rides.
 * - Shows incoming requests ONLY for the selected ride.
 * - Allows approving requests & Chat.
 * - Updates History immediately upon approval.
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
        loadHistory(); // Load history on startup too
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
        
        // Button Panel for Post Ride + Chat
        JPanel leftBtnPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JButton postRideBtn = new JButton("Post New Ride");
        postRideBtn.addActionListener(e -> new CampusCommuteGUI().setVisible(true));
        
        JButton chatBtn = new JButton("Ride Chat");
        chatBtn.addActionListener(e -> {
            int index = rideListUI.getSelectedIndex();
            if (index != -1) {
                Ride r = driverRides.get(index);
                new ChatGUI(r.getId()); 
            } else {
                JOptionPane.showMessageDialog(this, "Select a ride to open chat.");
            }
        });

        leftBtnPanel.add(postRideBtn);
        leftBtnPanel.add(chatBtn);
        
        leftPanel.add(leftBtnPanel, BorderLayout.SOUTH);

        // RIGHT: Incoming Requests
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Incoming Requests (Select a Ride)"));
        
        requestListModel = new DefaultListModel<>();
        requestListUI = new JList<>(requestListModel);
        requestListUI.setCellRenderer(new RequestRenderer());
        
        rightPanel.add(new JScrollPane(requestListUI), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton approveBtn = new JButton("Approve Request");
        JButton refreshBtn = new JButton("Refresh Data");
        
        approveBtn.setBackground(new Color(144, 238, 144)); 
        approveBtn.setOpaque(true);
        approveBtn.setBorderPainted(false);

        approveBtn.addActionListener(e -> approveSelectedRequest());
        refreshBtn.addActionListener(e -> {
            loadDriverData();
            loadHistory(); 
        });

        btnPanel.add(approveBtn);
        btnPanel.add(refreshBtn);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        panel.add(leftPanel);
        panel.add(rightPanel);
        
        // --- LISTENER: Load requests when a ride is selected ---
        rideListUI.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadRequestsForSelectedRide();
            }
        });

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
        requestListModel.clear();
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
                        // Filter out COMPLETED rides from the active list
                        if (!"COMPLETED".equalsIgnoreCase(r.getStatus())) {
                            String rideStr = String.format("%s -> %s @ %s | Seats: %d | $%.2f",
                                    r.getStartLocation(),
                                    r.getDestination(),
                                    r.getDepartureTime(),
                                    r.getAvailableSeats(),
                                    r.getPrice());
                            
                            driverRides.add(r);
                            rideListModel.addElement(rideStr);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadRequestsForSelectedRide() {
        requestListModel.clear();
        
        int index = rideListUI.getSelectedIndex();
        if (index == -1) {
            return; 
        }
        
        Ride selectedRide = driverRides.get(index);
        List<RideRequest> requests = selectedRide.getRequests();
        
        if (requests != null) {
            for (RideRequest req : requests) {
                if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                    requestListModel.addElement(req);
                }
            }
        }
        
        if (requestListModel.isEmpty()) {
             RideRequest placeholder = new RideRequest(0L, "No pending requests");
             requestListModel.addElement(placeholder);
        }
    }

    private void loadHistory() {
        try {
            Long driverId = CurrentUser.getId();
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/history/driver/" + driverId))
                    .GET().build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            
            if (resp.statusCode() == 200) {
                Ride[] history = gson.fromJson(resp.body(), Ride[].class);
                historyModel.setRowCount(0); 
                
                if (history != null) {
                    for (Ride r : history) {
                        String ratingStr = (r.getDriverRating() > 0) ? String.format("%.1f", r.getDriverRating()) : "-";
                        historyModel.addRow(new Object[]{
                            r.getDate(), 
                            r.getStartLocation(), 
                            r.getDestination(), 
                            "$" + r.getPrice(), 
                            ratingStr
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading history.");
        }
    }

    // =================================================================
    // APPROVE LOGIC (FIXED TO MATCH NEW BACKEND ENDPOINT)
    // =================================================================

    private void approveSelectedRequest() {
        // 1. Check if a ride is selected
        int rideIndex = rideListUI.getSelectedIndex();
        if (rideIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ride first.");
            return;
        }

        // 2. Check if a request is selected
        RideRequest selected = requestListUI.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.");
            return;
        }
        
        if (selected.getPassengerId() == 0L) return; // Ignore placeholder

        // 3. Gather Data
        Long reqId = selected.getId();

        try {
            // --- FIX: Use the new simple URL structure from RideController ---
            // Endpoint: /api/requests/{reqId}/status
            String urlStr = String.format("http://localhost:8080/api/requests/%d/status", reqId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    // Send "APPROVED" as raw text in the body
                    .POST(HttpRequest.BodyPublishers.ofString("APPROVED"))
                    .header("Content-Type", "text/plain") 
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Request Approved! Seat count updated.");
                
                // Remove from list locally
                requestListModel.removeElement(selected);
                
                // Refresh data (updates seats, history, etc.)
                loadMyRides();
                loadHistory(); 
                
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
                if (req.getPassengerId() == 0L) {
                    setText(req.getPassengerName()); 
                } else {
                    setText("Passenger: " + req.getPassengerName() + " (ID: " + req.getPassengerId() + ")");
                }
            }
            return this;
        }
    }
}