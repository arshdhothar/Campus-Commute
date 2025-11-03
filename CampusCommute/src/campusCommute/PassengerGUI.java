package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * GUI class for passengers to search available rides
 * and send ride requests to drivers.
 */
 
public class PassengerGUI extends JFrame {
	
	// UI components
    private JTextField searchField;
    private DefaultListModel<String> resultsModel;
    private JList<String> resultsList;
    private JButton requestButton;
    
    // Data models
    private List<Ride> rides;
    private Passenger passenger;
    private List<Ride> searchResults;
    
    /**
     * Constructor initializes UI layout and event handling.
     */

    public PassengerGUI(List<Ride> rides) {
        this.rides = rides;
        this.passenger = new Passenger("Guest");

        setTitle("Passenger - Search Rides");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panel);

        //  Search bar
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search by location, time, or destination:"));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        //  Results
        resultsModel = new DefaultListModel<>();
        resultsList = new JList<>(resultsModel);
        resultsList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Matching Rides"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Request button
        requestButton = new JButton("Request Selected Ride");
        requestButton.setEnabled(false);
        panel.add(requestButton, BorderLayout.SOUTH);

        // Actions
        searchButton.addActionListener(e -> searchRides());
        resultsList.addListSelectionListener(e -> requestButton.setEnabled(!resultsList.isSelectionEmpty()));
        requestButton.addActionListener(e -> requestRide());

        setVisible(true);
    }

    /**
     * Searches rides using Passenger.searchRides() and updates the list.
     * Includes error handling and empty input checks.
     */
    private void searchRides() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a location, time, or destination before searching.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            searchResults = passenger.searchRides(rides, query);
            resultsModel.clear();

            if (searchResults == null || searchResults.isEmpty()) {
                resultsModel.addElement("No rides found for: " + query);
                requestButton.setEnabled(false);
            } else {
                for (Ride r : searchResults) {
                    resultsModel.addElement(r.toString());
                }
                requestButton.setEnabled(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred while searching: " + ex.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    /**
     * Sends a ride request for the selected ride in the list.
     */
    private void requestRide() {
        int selectedIndex = resultsList.getSelectedIndex();
        if (selectedIndex == -1 || searchResults == null || searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a ride to request!");
            return;
        }

        Ride selectedRide = searchResults.get(selectedIndex);
        RideRequest request = new RideRequest(passenger.getName());
        selectedRide.addRequest(request);

        JOptionPane.showMessageDialog(this,
                "Ride request sent to driver for: " + selectedRide.getDestination(),
                "Request Sent",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Simple test harness with mock data.
     */
    
    public static void main(String[] args) {
        // For testing
        List<Ride> rides = new ArrayList<>();
        rides.add(new Ride("UTSC", "Downtown", "9:00 AM", 3, 10.0));
        rides.add(new Ride("Scarborough", "Mississauga", "8:30 AM", 2, 12.5));
        SwingUtilities.invokeLater(() -> new PassengerGUI(rides));
    }
}
