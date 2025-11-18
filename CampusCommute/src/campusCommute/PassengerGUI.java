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
 * PassengerGUI – Merged
 * - Modern UI with tabs (Search / History / My Profile)
 * - Backend integration (load rides, send requests, history)
 * - Classic search list + "Request Selected Ride" button (from v1)
 * - Emergency button panel on Search tab bottom bar (from v1)
 * - Supports both backend and local (legacy) operation
 */
public class PassengerGUI extends JFrame {

    // -------- Backend helpers --------
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    // -------- Common / legacy data --------
    private Passenger passenger = new Passenger("Guest"); // v1 behavior for legacy/local use
    private List<Ride> providedRides = new ArrayList<>(); // from v1 constructor
    private List<Ride> allRides = new ArrayList<>();      // backend rides
    private List<Ride> filtered = new ArrayList<>();      // merged filtering target

    // -------- SEARCH (modern cards) --------
    private JTextField searchField;
    private JPanel resultsContainer; // cards

    // -------- SEARCH (classic list + request button) --------
    private DefaultListModel<String> resultsModel;
    private JList<String> resultsList;
    private JButton requestButton;

    // emergency panel (v1)
    private EmergencyButtonPanel emergencyPanel;

    // classic list's backing results (v1)
    private List<Ride> searchResults = new ArrayList<>();

    // -------- HISTORY --------
    private DefaultTableModel historyModel;

    // -------- Constructors --------

    /** Legacy-style constructor (v1) that accepts local rides and does NOT require login. */
    public PassengerGUI(List<Ride> rides) {
        this.providedRides = (rides != null) ? rides : new ArrayList<>();
        initUI(/*allowBackend=*/CurrentUser.loggedInUser != null);
    }

    /** Modern constructor (v2) expecting a logged-in user (but still works if not). */
    public PassengerGUI() {
        initUI(/*allowBackend=*/CurrentUser.loggedInUser != null);
    }

    // -------- UI Setup --------
    private void initUI(boolean allowBackend) {
        setTitle("Passenger Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(tabs);

        // ===================== TAB 1: SEARCH =====================
        JPanel searchPanel = new JPanel(new BorderLayout(15, 15));
        searchPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabs.addTab("Search Rides", searchPanel);

        // Top search bar
        JPanel searchBar = new JPanel(new FlowLayout());
        searchBar.add(new JLabel("Search (location/time/destination): "));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchBar.add(searchField);
        searchBar.add(searchButton);
        searchPanel.add(searchBar, BorderLayout.NORTH);

        // Center split: left modern results (cards), right classic list (v1)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.6);

        // Modern card container (left)
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        JScrollPane cardsScroll = new JScrollPane(resultsContainer);
        cardsScroll.setBorder(BorderFactory.createTitledBorder("Matching Ride Cards"));

        // Classic list (right) from v1
        JPanel classicPanel = new JPanel(new BorderLayout(8, 8));
        classicPanel.setBorder(BorderFactory.createTitledBorder("Matching Rides (Classic List)"));
        resultsModel = new DefaultListModel<>();
        resultsList = new JList<>(resultsModel);
        resultsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classicPanel.add(new JScrollPane(resultsList), BorderLayout.CENTER);

        split.setLeftComponent(cardsScroll);
        split.setRightComponent(classicPanel);
        searchPanel.add(split, BorderLayout.CENTER);

        // Bottom bar: left "Request Selected Ride" (v1) + right Emergency panel (v1)
        JPanel bottomBar = new JPanel(new BorderLayout(10, 0));

        requestButton = new JButton("Request Selected Ride");
        requestButton.setEnabled(false);
        bottomBar.add(requestButton, BorderLayout.WEST);



        String userNameForEmergency = (CurrentUser.loggedInUser != null)
                ? CurrentUser.loggedInUser.getfName()
                : passenger.getName();
        String userPhoneForEmergency = "N/A"; // placeholder unless you have it
        emergencyPanel = new EmergencyButtonPanel(
                userNameForEmergency,
                userPhoneForEmergency,
                "NO_SELECTION" // updated on list selection
        );
        bottomBar.add(emergencyPanel, BorderLayout.EAST);
        searchPanel.add(bottomBar, BorderLayout.SOUTH);

        // Wire actions
        searchButton.addActionListener(e -> searchRides(allowBackend));
        resultsList.addListSelectionListener(e -> {
            boolean hasSel = !resultsList.isSelectionEmpty();
            requestButton.setEnabled(hasSel);
            if (hasSel) {
                int idx = resultsList.getSelectedIndex();
                if (idx >= 0 && idx < searchResults.size()) {
                    Ride r = searchResults.get(idx);
                    emergencyPanel.setRideId(String.valueOf(r.getId())); // keep it updated
                }
            } else {
                emergencyPanel.setRideId("NO_SELECTION");
            }
        });

        requestButton.addActionListener(e -> requestSelectedRide(allowBackend));

        // If backend allowed, load rides; otherwise we rely on providedRides
        if (allowBackend) {
            loadAllRides();
        }

        // ===================== TAB 2: HISTORY =====================
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

        // ===================== TAB 3: MY PROFILE =====================
        JPanel profilePlaceholderPanel = new JPanel();
        tabs.addTab("My Profile", profilePlaceholderPanel);

        // Tab change behavior (load history; open profile as window)
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();

            if (selectedIndex == 1) { // Ride History
                if (CurrentUser.loggedInUser != null) {
                    loadRideHistory();
                } else {
                    historyModel.setRowCount(0);
                    historyModel.addRow(new Object[]{"Login required", "", "", ""});
                }
            } else if (selectedIndex == 2) { // My Profile
                if (CurrentUser.loggedInUser != null) {
                    new ViewProfileGUI(CurrentUser.loggedInUser);
                } else {
                    JOptionPane.showMessageDialog(this, "You must be logged in to view your profile.");
                }
                // Return to Search tab
                tabs.setSelectedIndex(0);
            }
        });

        setVisible(true);
    }

    // ===================== BACKEND: Load all rides =====================
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

    // ===================== SEARCH (merged) =====================
    private void searchRides(boolean allowBackend) {
        String q = searchField.getText().trim().toLowerCase();

        // Clear UI containers
        resultsContainer.removeAll();
        resultsModel.clear();
        filtered.clear();
        searchResults.clear();

        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.");
            resultsContainer.revalidate();
            resultsContainer.repaint();
            return;
        }

        // Build the data set: backend rides + provided local rides (avoid duplicates by id if possible)
        List<Ride> source = new ArrayList<>();
        if (allowBackend) source.addAll(allRides);
        if (providedRides != null) source.addAll(providedRides);

        for (Ride r : source) {
            try {
                String from = (r.getStartLocation() != null) ? r.getStartLocation() : "";
                String to = (r.getDestination() != null) ? r.getDestination() : "";
                String time = (r.getDepartureTime() != null) ? r.getDepartureTime() : "";

                if (from.toLowerCase().contains(q)
                        || to.toLowerCase().contains(q)
                        || time.toLowerCase().contains(q)) {

                    // modern cards
                    filtered.add(r);
                    resultsContainer.add(createRideCard(r, allowBackend));

                    // classic list
                    searchResults.add(r);
                    resultsModel.addElement(r.toString());
                }
            } catch (Exception ignore) {
                // keep searching other rides
            }
        }

        if (filtered.isEmpty()) {
            JLabel noRes = new JLabel("No rides found.");
            noRes.setFont(new Font("Segoe UI", Font.BOLD, 18));
            resultsContainer.add(noRes);

            resultsModel.addElement("No rides found for: " + q);
            requestButton.setEnabled(false);
        }

        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    // ===================== RIDE CARD UI (modern) =====================
    private JPanel createRideCard(Ride r, boolean allowBackend) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(750, 150));
        card.setBackground(new Color(250, 250, 250));

        JLabel title = new JLabel(safe(r.getStartLocation()) + " \u2192 " + safe(r.getDestination()));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel time = new JLabel("Departure: " + safe(r.getDepartureTime()));
        time.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel price = new JLabel("Price: $" + r.getPrice());
        price.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel info = new JPanel(new GridLayout(3, 1));
        info.setOpaque(false);
        info.add(title);
        info.add(time);
        info.add(price);

        card.add(info, BorderLayout.CENTER);

        JButton requestBtn = new JButton("Request Ride");
        requestBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        requestBtn.setPreferredSize(new Dimension(120, 30));
        requestBtn.setFocusPainted(false);
        requestBtn.setBackground(Color.WHITE);
        requestBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));

        requestBtn.addActionListener(e -> {
            if (allowBackend && CurrentUser.loggedInUser != null) {
                sendRideRequest(r); // backend POST
            } else {
                requestRideLegacy(r); // local addRequest flow
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(requestBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    // ===================== Classic "Request Selected Ride" (v1) =====================
    private void requestSelectedRide(boolean allowBackend) {
        int selectedIndex = resultsList.getSelectedIndex();
        if (selectedIndex == -1 || searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a ride to request!");
            return;
        }

        Ride selectedRide = searchResults.get(selectedIndex);
        if (allowBackend && CurrentUser.loggedInUser != null) {
            sendRideRequest(selectedRide);
        } else {
            requestRideLegacy(selectedRide);
        }
    }

    // ===================== Legacy local request (v1) =====================
    private void requestRideLegacy(Ride ride) {
        try {
        	RideRequest request = new RideRequest(0L, passenger.getName());

            ride.addRequest(request);

            JOptionPane.showMessageDialog(this,
                    "Ride request sent to driver for: " + ride.getDestination(),
                    "Request Sent",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to send local request.");
        }
    }

    // ===================== Backend send request (v2) =====================
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

    // ===================== Load passenger ride history (v2) =====================
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

    // ===================== v1 test harness main =====================
    public static void main(String[] args) {
        // For quick local testing (legacy mode) without login/server:
        List<Ride> rides = new ArrayList<>();
        rides.add(new Ride("UTSC", "Downtown", "9:00 AM", 3, 10.0));
        rides.add(new Ride("Scarborough", "Mississauga", "8:30 AM", 2, 12.5));

        SwingUtilities.invokeLater(() -> new PassengerGUI(rides));
    }
}
