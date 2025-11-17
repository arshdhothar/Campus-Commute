package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

/**
 * PassengerGUI – Modern UI + Full Backend Support
 * --- NOW INCLUDES 'MY PROFILE' TAB ---
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

    public PassengerGUI() {

        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in.");
            dispose();
            return;
        }

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

        String[] cols = {"Date", "From", "To", "Price"};
        historyModel = new DefaultTableModel(cols, 0);

        JTable historyTable = new JTable(historyModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setRowHeight(24);

        histPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // -------------------------------------------------------
        // --- NEW --- TAB 3: MY PROFILE (opens new window)
        // -------------------------------------------------------
        JPanel profilePlaceholderPanel = new JPanel();
        tabs.addTab("My Profile", profilePlaceholderPanel);
        

        // --- MODIFIED ChangeListener ---
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            
            if (selectedIndex == 1) {
                // User clicked "Ride History"
                loadRideHistory();
            } else if (selectedIndex == 2) {
                // User clicked "My Profile"
                // Open the ViewProfileGUI as a new window
                new ViewProfileGUI(CurrentUser.loggedInUser);
                
                // Set the tab back to "Search Rides"
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
        card.setMaximumSize(new Dimension(750, 170));
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

        // =====================================================
        // BUTTONS PANEL (Start, Destination, Request)
        // =====================================================

        JButton startBtn = new JButton("Start Location");
        JButton destBtn = new JButton("Destination");
        JButton requestBtn = new JButton("Request Ride");  // <-- same as before

        Font btnFont = new Font("Segoe UI", Font.PLAIN, 13);
        startBtn.setFont(btnFont);
        destBtn.setFont(btnFont);
        requestBtn.setFont(btnFont);

        JButton[] buttons = {startBtn, destBtn, requestBtn};
        for (JButton b : buttons) {
            b.setPreferredSize(new Dimension(140, 30));
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        }

        // ---- ACTIONS ----
        // New: open maps for start / destination
        startBtn.addActionListener(e -> openInMaps(r.getStartLocation()));
        destBtn.addActionListener(e -> openInMaps(r.getDestination()));

        // Existing logic preserved: still calls sendRideRequest(r)
        requestBtn.addActionListener(e -> sendRideRequest(r));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(startBtn);
        buttonPanel.add(destBtn);
        buttonPanel.add(requestBtn);

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
                historyModel.addRow(new Object[]{
                        r.getDate(),
                        r.getStartLocation(),
                        r.getDestination(),
                        "$" + r.getPrice()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading history.");
        }
    }

    //This method is used to oepn maps in a browser
     private void openInMaps(String location) {
         try {
             String query = java.net.URLEncoder.encode(location, java.nio.charset.StandardCharsets.UTF_8);
             String url = "https://maps.google.com/?q=" + query;
             Desktop.getDesktop().browse(new URI(url));
         } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Could not open browser.");
         }
     }

}