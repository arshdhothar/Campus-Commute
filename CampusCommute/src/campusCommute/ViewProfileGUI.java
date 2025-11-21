package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Displays a read-only view of the user's profile.
 * Now includes the Driver Rating display.
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
    
    // New component for rating
    private JLabel ratingLabel;

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

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        nameLabel = createValueLabel(CurrentUser.getName());
        emailLabel = createValueLabel(currentUser.getEmail());
        phoneLabel = createValueLabel(currentUser.getPhone());
        userTypeLabel = createValueLabel(currentUser.getUserType());
        
        // Initialize the rating label with the calculated value
        ratingLabel = createValueLabel(getFormattedRating());

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(nameLabel);
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(emailLabel);
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(phoneLabel);
        infoPanel.add(new JLabel("User Type:"));
        infoPanel.add(userTypeLabel);
        
        // Add the Rating row
        infoPanel.add(new JLabel("Driver Rating:"));
        infoPanel.add(ratingLabel);

        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // Bio Section
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioPanel.add(new JLabel("Bio:"), BorderLayout.NORTH);
        bioTextArea = new JTextArea(currentUser.getBio());
        bioTextArea.setEditable(false);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setBackground(this.getBackground()); 
        bioPanel.add(new JScrollPane(bioTextArea), BorderLayout.CENTER);
        
        centerPanel.add(bioPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton editButton = new JButton("Edit Profile");
        editButton.addActionListener(this::openEditProfile);
        buttonPanel.add(editButton);

        // Context-aware buttons
        if (currentUser.getUserType().equalsIgnoreCase("driver") || currentUser.getUserType().equalsIgnoreCase("both")) {
            JButton driverBtn = new JButton("Driver Dashboard");
            driverBtn.addActionListener(e -> openDriverGUI());
            buttonPanel.add(driverBtn);
        }
        
        if (currentUser.getUserType().equalsIgnoreCase("user") || currentUser.getUserType().equalsIgnoreCase("both")) {
            JButton passengerBtn = new JButton("Passenger Dashboard");
            passengerBtn.addActionListener(e -> openPassengerGUI());
            buttonPanel.add(passengerBtn);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    // Helper to format the rating string
    private String getFormattedRating() {
        if (currentUser.getRatingCount() > 0) {
            return String.format("%.1f / 5.0 (%d ratings)", currentUser.getAverageRating(), currentUser.getRatingCount());
        } else {
            return "No ratings yet";
        }
    }

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

    private void openEditProfile(ActionEvent e) {
        new ProfileCustomizationGUI(this.currentUser);
        this.dispose(); 
    }
    
    private void openDriverGUI() {
        new CampusCommuteGUI().setVisible(true);
    }

    private void openPassengerGUI() {
        new PassengerGUI().setVisible(true);
    }
}