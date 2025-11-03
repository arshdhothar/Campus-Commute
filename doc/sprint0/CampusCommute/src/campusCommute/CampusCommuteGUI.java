package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CampusCommuteGUI extends JFrame {
    private JTextField startField, destinationField, timeField, seatsField, priceField;
    private DefaultListModel<String> rideListModel;
    private JList<String> rideList;
    private List<Ride> rides;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CampusCommuteGUI().setVisible(true));
    }

    public CampusCommuteGUI() {
        rides = new ArrayList<>();

        setTitle("Campus Commute - Driver Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panel);

        JLabel title = new JLabel("Campus Commute - Driver", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // 🔹 Input panel for adding rides
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Ride"));

        startField = new JTextField();
        destinationField = new JTextField();
        timeField = new JTextField();
        seatsField = new JTextField();
        priceField = new JTextField();

        inputPanel.add(new JLabel("Start Location:"));
        inputPanel.add(startField);
        inputPanel.add(new JLabel("Destination:"));
        inputPanel.add(destinationField);
        inputPanel.add(new JLabel("Departure Time:"));
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("Available Seats:"));
        inputPanel.add(seatsField);
        inputPanel.add(new JLabel("Price ($):"));
        inputPanel.add(priceField);

        JButton addRideButton = new JButton("Add Ride");
        JButton passengerViewButton = new JButton("Open Passenger View");
        JButton viewRequestsButton = new JButton("View Ride Requests");

        inputPanel.add(addRideButton);
        inputPanel.add(passengerViewButton);
        inputPanel.add(viewRequestsButton);
        panel.add(inputPanel, BorderLayout.WEST);

        // 🔹 Ride list
        rideListModel = new DefaultListModel<>();
        rideList = new JList<>(rideListModel);
        rideList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(rideList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Your Rides"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 🔹 Actions
        addRideButton.addActionListener(e -> addRide());
        passengerViewButton.addActionListener(e -> new PassengerGUI(rides));
        viewRequestsButton.addActionListener(e -> showRequests());
    }

    private void addRide() {
        try {
            String start = startField.getText().trim();
            String dest = destinationField.getText().trim();
            String time = timeField.getText().trim();
            int seats = Integer.parseInt(seatsField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            if (start.isEmpty() || dest.isEmpty() || time.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            Ride ride = new Ride(start, dest, time, seats, price);
            rides.add(ride);
            rideListModel.addElement(ride.toString());

            startField.setText("");
            destinationField.setText("");
            timeField.setText("");
            seatsField.setText("");
            priceField.setText("");

            JOptionPane.showMessageDialog(this, "Ride added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats and Price must be numeric!");
        }
    }

    // 🧩 Updated: View Requests with Approve/Decline buttons
    private void showRequests() {
        JFrame reqFrame = new JFrame("Ride Requests");
        reqFrame.setSize(600, 400);
        reqFrame.setLocationRelativeTo(this);

        DefaultListModel<String> reqModel = new DefaultListModel<>();
        JList<String> reqList = new JList<>(reqModel);
        reqList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Collect all requests
        List<RideRequest> allRequests = new ArrayList<>();
        List<Ride> rideMapping = new ArrayList<>();

        for (Ride r : rides) {
            for (RideRequest req : r.getRequests()) {
                allRequests.add(req);
                rideMapping.add(r);
                String status = req.isApproved() ? "✅ Approved" : "⏳ Pending";
                reqModel.addElement(req.getPassengerName() + " requested " + r.getDestination() + " " + status);
            }
        }

        if (reqModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No ride requests yet.");
            return;
        }

        JScrollPane scrollPane = new JScrollPane(reqList);
        reqFrame.add(scrollPane, BorderLayout.CENTER);

        JButton approveButton = new JButton("Approve");
        JButton declineButton = new JButton("Decline");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(declineButton);
        reqFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 🔹 Actions
        approveButton.addActionListener(e -> {
            int index = reqList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(reqFrame, "Select a request to approve!");
                return;
            }
            RideRequest request = allRequests.get(index);
            Ride ride = rideMapping.get(index);

            if (ride.getAvailableSeats() > 0 && !request.isApproved()) {
                ride.approveRequest(request);
                rideListModel.setElementAt(ride.toString(), rides.indexOf(ride));
                reqModel.setElementAt(request.getPassengerName() + " requested " + ride.getDestination() + " ✅ Approved", index);
                JOptionPane.showMessageDialog(reqFrame, "Request approved! Seat count updated.");
            } else {
                JOptionPane.showMessageDialog(reqFrame, "No available seats or already approved.");
            }
        });

        declineButton.addActionListener(e -> {
            int index = reqList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(reqFrame, "Select a request to decline!");
                return;
            }
            RideRequest request = allRequests.get(index);
            Ride ride = rideMapping.get(index);
            ride.declineRequest(request);
            reqModel.setElementAt(request.getPassengerName() + " requested " + ride.getDestination() + " ❌ Declined", index);
            JOptionPane.showMessageDialog(reqFrame, "Request declined.");
        });

        reqFrame.setVisible(true);
    }
}
