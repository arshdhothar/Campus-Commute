package campusCommute;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


/**
 * CampusCommuteGUI
 *
 * A simple GUI for adding and viewing available rides.
 * 
 * Users can input ride details (start, destination, time, seats, price)
 * and add them to a list displayed on the right-hand side of the window.
 */
public class CampusCommuteGUI extends JFrame {
	
    // GUI components
	private JPanel contentPane;
	private JTextField startField, destinationField, timeField, seatsField, priceField;
	private DefaultListModel<String> rideListModel;
	private JList<String> rideList;
	
	// Data model
	private List<Ride> rides;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CampusCommuteGUI frame = new CampusCommuteGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CampusCommuteGUI() {
		// Initialize the in-memory ride list 
		rides  = new ArrayList<>();
		
		// Frame configuration
		setTitle("Campus Commute");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 450);
		setLocationRelativeTo(null);
		
		// Main content panel
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(new Color(245, 245, 245));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		setContentPane(panel);
		
		// Title label at the top
		JLabel title = new JLabel("Campus Commute", SwingConstants.CENTER);
		title.setFont(new Font("Segoe UI", Font.BOLD, 20));
		panel.add(title, BorderLayout.NORTH);
		
		// Panel for user input fields
		JPanel inputPanel = new JPanel(new GridLayout(6, 2, 8, 8));
		inputPanel.setBackground(Color.WHITE);
		inputPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Add Ride"), 
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		inputPanel.add(new JLabel("Start Location:"));
		startField = new JTextField();
		inputPanel.add(startField);
		
		inputPanel.add(new JLabel("Destination"));
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
		
		JButton addRideButton = new JButton("Add Ride");
		addRideButton.setForeground(new Color(30, 144, 255));
		addRideButton.setFocusPainted(false);
		addRideButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		inputPanel.add(addRideButton);
		panel.add(inputPanel, BorderLayout.WEST);
		
		rideListModel = new DefaultListModel<>();
		JList<String> rideList = new JList<>(rideListModel);
		rideList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(rideList);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Available Rides"));
		panel.add(scrollPane, BorderLayout.CENTER);
		
		addRideButton.addActionListener(e -> addRide());
	}
	
	/**u
	 * Handles adding a new ride to the list.
	 * Reads input values, validates them, creates a new Ride, and updates the display.
	 */
	private void addRide() {
		try {
			
			//Take user input
			String start = startField.getText();
			String dest = destinationField.getText();
			String time = timeField.getText();
			int seats = Integer.parseInt(seatsField.getText());
			double price = Double.parseDouble(priceField.getText());
			
			//Create a ride in the list
			Ride ride = new Ride(start, dest, time, seats, price);
			rides.add(ride);
			rideListModel.addElement(ride.toString());
			
			//Clear fields so a new ride can be entered
			startField.setText("");
			destinationField.setText("");
			timeField.setText("");
			seatsField.setText("");
			priceField.setText("");
			
			//Success message
			JOptionPane.showMessageDialog(this,  "Ride added successfully!");
			
		} catch (NumberFormatException ex) {
			//Invalid input message
			JOptionPane.showMessageDialog(this, "Seats and Price must be numeric");
		}
	}
}
