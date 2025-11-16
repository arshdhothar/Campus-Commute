package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Displays a read-only view of the user's profile.
 * Provides a button to switch to the ProfileCustomizationGUI.
 */
public class ViewProfileGUI extends JFrame {

    private Person currentUser;

    // GUI Components
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel userTypeLabel;
    private JTextArea bioTextArea;
    private JLabel photoLabel;

    public ViewProfileGUI(Person currentUser) {
        this.currentUser = currentUser;

        setTitle("My Profile");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Profile Photo Panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));
        photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loadPhoto(currentUser.getPhotoPath());
        leftPanel.add(photoLabel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);

        // User Details Panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(20, 10, 20, 20));

        // Panel for name, email, phone, etc.
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Name:"), gbc);
        gbc.gridy++;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        infoPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridy++;
        infoPanel.add(new JLabel("User Type:"), gbc);

        // Values
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        nameLabel = createValueLabel(currentUser.getfName() + " " + currentUser.getlName());
        infoPanel.add(nameLabel, gbc);
        
        gbc.gridy++;
        emailLabel = createValueLabel(currentUser.getEmail());
        infoPanel.add(emailLabel, gbc);

        gbc.gridy++;
        phoneLabel = createValueLabel(currentUser.getPhone());
        infoPanel.add(phoneLabel, gbc);
        
        gbc.gridy++;
        userTypeLabel = createValueLabel(currentUser.getUserType());
        infoPanel.add(userTypeLabel, gbc);

        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // Bio Panel
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.add(new JLabel("Your Bio:"), BorderLayout.NORTH);
        bioTextArea = new JTextArea(currentUser.getBio());
        bioTextArea.setEditable(false);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setBackground(getBackground()); // Make it look like a label
        bioTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(bioTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        bioPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(bioPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        
        JButton offerRideButton = new JButton("Offer a Ride");
        offerRideButton.addActionListener(e -> openDriverGUI());
        buttonPanel.add(offerRideButton);

        JButton findRideButton = new JButton("Find a Ride");
        findRideButton.addActionListener(e -> openPassengerGUI());
        buttonPanel.add(findRideButton);
        
        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.addActionListener(this::openEditProfile);
        buttonPanel.add(editProfileButton);
        
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Helper for creating formatted value labels
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    // Loads the user's photo into the photoLabel
    private void loadPhoto(String photoPath) {
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(photoPath);
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            } catch (Exception e) {
                photoLabel.setText("Photo not found");
            }
        } else {
            photoLabel.setText("No photo selected");
        }
    }

    // Closes this window and opens the edit profile window
    private void openEditProfile(ActionEvent e) {
        new ProfileCustomizationGUI(this.currentUser);
        this.dispose(); 
    }
    
    /**
     * Opens the GUI for drivers (CampusCommuteGUI).
     */
    private void openDriverGUI() {
        // This opens the window for posting a new ride
        new CampusCommuteGUI().setVisible(true);
    }

    /**
     * Opens the GUI for passengers (PassengerGUI).
     */
    private void openPassengerGUI() {
        // This now uses the correct, no-argument constructor
        new PassengerGUI().setVisible(true);
    }
}