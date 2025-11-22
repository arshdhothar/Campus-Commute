package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * PassengerGUI – Modern UI + Full Backend Support
 * --- NOW INCLUDES CHAT BUTTON ---
 */
public class PassengerGUI extends JFrame {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // SEARCH COMPONENTS
    private JTextField searchField;
    private JPanel resultsContainer;

    private List<Ride> allRides = new ArrayList<>();
    private List<Ride> filtered = new ArrayList<>();

    // HISTORY COMPONENTS
    private DefaultTableModel historyModel;
    private JTable historyTable;

    // --- Needed for search logic ---
    private Passenger passenger; 

    public PassengerGUI() {

        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in.");
            dispose();
            return;
        }
        
        // Initialize passenger helper
        this.passenger = new Passenger(CurrentUser.getName());

        setTitle("Passenger Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(tabs);

        // ---------------- TAB 1: SEARCH ----------------
        JPanel searchPanel = new JPanel(new BorderLayout(15, 15));
        searchPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabs.addTab("Search Rides", searchPanel);

        JPanel searchBar = new JPanel(new FlowLayout());
        searchBar.add(new JLabel("Search (location/time/destination): "));

        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));

        searchBar.add(searchField);
        searchBar.add(searchButton);

        searchPanel.add(searchBar, BorderLayout.NORTH);

        // RESULTS AREA (modern ride cards)
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> searchRides());
        loadAllRides();  // load backend data

        // ---------------- TAB 2: HISTORY ----------------
        JPanel histPanel = new JPanel(new BorderLayout(15, 15));
        histPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabs.addTab("Ride History", histPanel);

        JLabel historyLabel = new JLabel("Your Completed Rides");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        histPanel.add(historyLabel, BorderLayout.NORTH);

        String[] cols = {"Date", "From", "To", "Price", "DriverID", "Action"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only button column is editable
            }
        };

        historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setRowHeight(24);
        
        // Hide DriverID column
        historyTable.getColumnModel().getColumn(4).setMinWidth(0);
        historyTable.getColumnModel().getColumn(4).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(4).setWidth(0);

        // Set up button renderer/editor
        historyTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        histPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("Refresh History");
        refreshBtn.addActionListener(e -> loadRideHistory());
        histPanel.add(refreshBtn, BorderLayout.SOUTH);

        // ---------------- TAB 3: MY PROFILE ----------------
        JPanel profileWrapper = new JPanel(new BorderLayout());
        JButton openProfileBtn = new JButton("Open My Full Profile");
        openProfileBtn.addActionListener(e -> new ViewProfileGUI(CurrentUser.loggedInUser).setVisible(true));
        JPanel center = new JPanel(new GridBagLayout());
        center.add(openProfileBtn);
        profileWrapper.add(center);
        tabs.addTab("My Profile", profileWrapper);

        // Listener for tabs
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex == 1) loadRideHistory();
            else if (selectedIndex == 2) {
                new ViewProfileGUI(CurrentUser.loggedInUser);
                tabs.setSelectedIndex(0);
            }
        });

        setVisible(true);
    }


    // ================================================================
    // LOAD ALL RIDES
    // ================================================================
    private void loadAllRides() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides"))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            Ride[] rides = gson.fromJson(response.body(), Ride[].class);

            allRides.clear();
            if (rides != null) {
                for (Ride r : rides) {
                    allRides.add(r);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not load rides from server.");
        }
    }


    // ================================================================
    // SEARCH
    // ================================================================
    private void searchRides() {
        String q = searchField.getText().trim().toLowerCase();

        resultsContainer.removeAll();
        filtered.clear();

        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.");
            return;
        }

        for (Ride r : allRides) {
            if (r.getStartLocation().toLowerCase().contains(q)
                    || r.getDestination().toLowerCase().contains(q)
                    || r.getDepartureTime().toLowerCase().contains(q)) {

                filtered.add(r);
                resultsContainer.add(createRideCard(r));
            }
        }

        if (filtered.isEmpty()) {
            JLabel noRes = new JLabel("No rides found.");
            noRes.setFont(new Font("Segoe UI", Font.BOLD, 18));
            resultsContainer.add(noRes);
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }


    // ================================================================
    // RIDE CARD UI
    // ================================================================
    private JPanel createRideCard(Ride r) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(750, 150));
        card.setBackground(new Color(250, 250, 250));

        // ---- TOP INFO (title, time, price) ----
        JLabel title = new JLabel(r.getStartLocation() + " → " + r.getDestination());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel time = new JLabel("Departure: " + r.getDepartureTime());
        time.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel price = new JLabel("Price: $" + r.getPrice());
        price.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);
        info.add(title);
        info.add(time);
        info.add(price);

        card.add(info, BorderLayout.CENTER);

        // ---- BUTTONS AT BOTTOM ----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        // Request Button
        JButton requestBtn = new JButton("Request Ride");
        requestBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        requestBtn.setBackground(Color.WHITE);
        requestBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        requestBtn.addActionListener(e -> sendRideRequest(r));
        
        // --- NEW CHAT BUTTON ---
        JButton chatBtn = new JButton("Chat");
        chatBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatBtn.setBackground(Color.WHITE);
        chatBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        // Open chat for this specific ride ID
        chatBtn.addActionListener(e -> new ChatGUI(r.getId()));

        buttonPanel.add(requestBtn);
        buttonPanel.add(chatBtn);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }


    // ================================================================
    // SEND REQUEST
    // ================================================================
    private void sendRideRequest(Ride ride) {
        try {
            long passengerId = CurrentUser.loggedInUser.getID();
            String passengerName = CurrentUser.loggedInUser.getfName();

            RideRequest reqBody = new RideRequest(passengerId, passengerName);

            String json = gson.toJson(reqBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/rides/" + ride.getId() + "/request"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JOptionPane.showMessageDialog(this, "Ride request sent!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send request.");
        }
    }


    // ================================================================
    // LOAD PASSENGER RIDE HISTORY
    // ================================================================
    private void loadRideHistory() {
        try {
            long passengerId = CurrentUser.loggedInUser.getID();

            URL url = new URL("http://localhost:8080/api/history/passenger/" + passengerId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            String json;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                json = br.lines().collect(Collectors.joining());
            }

            Ride[] rides = gson.fromJson(json, Ride[].class);

            historyModel.setRowCount(0);

            if (rides == null || rides.length == 0) {
                historyModel.addRow(new Object[]{"No rides", "", "", ""});
                return;
            }

            for (Ride r : rides) {
                String driverIdStr = (r.getDriverId() != null) ? String.valueOf(r.getDriverId()) : "0";
                String actionText = r.getStatus();

                boolean isRateable = "APPROVED".equalsIgnoreCase(r.getStatus()) || 
                                     "COMPLETED".equalsIgnoreCase(r.getStatus());

                if (isRateable && r.getDriverId() != null) {
                    actionText = "Rate Driver";
                }

                historyModel.addRow(new Object[]{
                        r.getDate(),
                        r.getStartLocation(),
                        r.getDestination(),
                        "$" + r.getPrice(),
                        driverIdStr,
                        actionText
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading history.");
        }
    }
    
    // --- Renderers for the Rate Driver button ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) { setText(""); setEnabled(false); }
            else { setText(value.toString()); setEnabled("Rate Driver".equals(value.toString())); }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && "Rate Driver".equals(label)) {
                String driverIdStr = (String) historyTable.getValueAt(currentRow, 4);
                Long driverId = Long.parseLong(driverIdStr);
                SwingUtilities.invokeLater(() -> showRatingDialog(driverId));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() { isPushed = false; return super.stopCellEditing(); }

        private void showRatingDialog(Long driverId) {
            String input = JOptionPane.showInputDialog(button, "Rate this driver (1.0 - 5.0):", "Rate Ride", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.isEmpty()) {
                try {
                    double rating = Double.parseDouble(input);
                    if (rating < 1.0 || rating > 5.0) JOptionPane.showMessageDialog(button, "Please enter a value between 1.0 and 5.0");
                    else {
                        boolean ok = RideService.getInstance().rateDriver(driverId, rating);
                        if (ok) JOptionPane.showMessageDialog(button, "Rating submitted! Thank you.");
                        else JOptionPane.showMessageDialog(button, "Failed to submit.");
                    }
                } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(button, "Invalid number format."); }
            }
        }
    }
}