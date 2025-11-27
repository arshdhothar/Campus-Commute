package campusCommute;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 * CampusCommuteGUI
 *
 * DRIVER UI for posting rides.
 * This version CONNECTS TO BACKEND.
 * - Posts rides to MySQL (via Spring Boot)
 * - Loads rides from backend on startup
 *
 * + Emergency button integrated (driver side)
 */
public class CampusCommuteGUI extends JFrame {

    // GUI components
    private JTextField startField, destinationField, timeField, seatsField, priceField;
    private DefaultListModel<String> rideListModel;
    private JList<String> rideList;

    // Emergency button panel (added)
    @SuppressWarnings("serial")
    private EmergencyButtonPanel emergencyPanel;

    // Backend + JSON
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // Local ride list (mirrors backend)
    private List<Ride> rides = new ArrayList<>();

    /**
     * Main launcher
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CampusCommuteGUI frame = new CampusCommuteGUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructor — sets up UI and loads rides from backend
     */
    public CampusCommuteGUI() {

        // Login guard (your project uses CurrentUser)
        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this,
                    "You must be logged in to post a ride.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        // Frame settings
        setTitle("Campus Commute — Driver Console");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        // MAIN PANEL
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panel);

        // TITLE
        JLabel title = new JLabel("Driver Ride Posting", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        // INPUT PANEL
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add New Ride"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        inputPanel.add(new JLabel("Start Location:"));
        startField = new JTextField();
        inputPanel.add(startField);

        inputPanel.add(new JLabel("Destination:"));
        destinationField = new JTextField();
        inputPanel.add(destinationField);

        inputPanel.add(new JLabel("Departure Time:"));
        timeField = new JTextField();
        inputPanel.add(timeField);

        inputPanel.add(new JLabel("Available Seats:"));
        seatsField = new JTextField();
        inputPanel.add(seatsField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        // POST BUTTON
        JButton addRideButton = new JButton("Post Ride");
        addRideButton.setForeground(new Color(30, 144, 255));
        addRideButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(addRideButton);

        // DRIVER PANEL BUTTON
        JButton driverPanelButton = new JButton("Open Driver Panel");
        driverPanelButton.setForeground(new Color(0, 140, 70));
        driverPanelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputPanel.add(driverPanelButton);

        panel.add(inputPanel, BorderLayout.WEST);

        // RIDE LIST DISPLAY
        rideListModel = new DefaultListModel<>();
        rideList = new JList<>(rideListModel);
        rideList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(rideList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Posted Rides"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Emergency bottom bar (always visible) ---
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));
        emergencyPanel = new EmergencyButtonPanel(
                "Driver",
                "N/A",
                "NO_SELECTION"
        );
        bottomBar.add(emergencyPanel, BorderLayout.EAST);
        panel.add(bottomBar, BorderLayout.SOUTH);


        // ACTIONS
        addRideButton.addActionListener(e -> addRide());
        driverPanelButton.addActionListener(e -> openDriverPanel());

        // Update emergency rideId when selection changes
        rideList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (!rideList.isSelectionEmpty() && rideList.getSelectedIndex() >= 0 && rideList.getSelectedIndex() < rides.size()) {
                    int idx = rideList.getSelectedIndex();
                    String rideId = resolveRideId(rides.get(idx), idx);
                    emergencyPanel.setRideId(rideId);
                } else {
                    emergencyPanel.setRideId("NO_SELECTION");
                }
            }
        });

        // Load rides and show window
        loadRidesFromBackend();
        setVisible(true);
    }

    /**
     * POST new ride to backend
     */
    private void addRide() {
        try {
            String start = startField.getText().trim();
            String dest = destinationField.getText().trim();
            String time = timeField.getText().trim();
            int seats = Integer.parseInt(seatsField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            Long driverId = CurrentUser.loggedInUser.getID();

            Ride ride = new Ride(start, dest, time, seats, price);
            ride.setDriverId(driverId);

            String json = gson.toJson(ride);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JOptionPane.showMessageDialog(this, "Ride posted to server!");

            // Refresh UI and keep selection + emergency rideId sane
            loadRidesFromBackend();
            if (rideListModel.getSize() > 0) {
                int last = rideListModel.getSize() - 1;
                rideList.setSelectedIndex(last);
                String rideId = resolveRideId(rides.get(last), last);
                emergencyPanel.setRideId(rideId);
            }

            // Clear fields
            startField.setText("");
            destinationField.setText("");
            timeField.setText("");
            seatsField.setText("");
            priceField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats must be a number and Price must be a decimal.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error posting ride: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * GET all rides from backend
     */
    private void loadRidesFromBackend() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Ride[] backendRides = gson.fromJson(response.body(), Ride[].class);

            rideListModel.clear();
            rides.clear();

            if (backendRides != null) {
                for (Ride r : backendRides) {
                    rides.add(r);
                    rideListModel.addElement(r.toString());
                }
            }

            // Auto-select first ride if available so emergency has a rideId ready
            if (!rides.isEmpty()) {
                rideList.setSelectedIndex(0);
                String firstRideId = resolveRideId(rides.get(0), 0);
                emergencyPanel.setRideId(firstRideId);
            } else {
                emergencyPanel.setRideId("NO_SELECTION");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load rides from server.");
            ex.printStackTrace();
            emergencyPanel.setRideId("NO_SELECTION");
        }
    }

    /**
     * Open backend-synced Driver Panel
     */
    private void openDriverPanel() {
        // Use invokeLater to ensure thread safety and wrap in try-catch to debug failures
        SwingUtilities.invokeLater(() -> {
            try {
                new DriverGUI().setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace(); // Print the real error to the console
                JOptionPane.showMessageDialog(this, "Failed to open Driver Panel: " + ex.getMessage());
            }
        });
    }

    /**
     * Helper to choose a stable rideId for emergency alerts.
     * If your Ride has a real ID getter (e.g., getId()), use it; otherwise fallback to index.
     */
    private String resolveRideId(Ride r, int index) {
        try {
            // If your Ride has a real getId() (Long/Integer/String), prefer that
            // Replace "getId" with the actual getter if different, e.g., getRideId()
            java.lang.reflect.Method m = r.getClass().getMethod("getId");
            Object id = m.invoke(r);
            if (id != null) return String.valueOf(id);
        } catch (Exception ignore) {
            // method not found or failed: fallback below
        }
        return "DRIVER_RIDE_" + index;
    }
}