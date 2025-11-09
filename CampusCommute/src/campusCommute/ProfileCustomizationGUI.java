package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ProfileCustomizationGUI extends JFrame {
    private JTextArea bioTextArea;
    private JLabel photoLabel;
    private JButton uploadButton, saveButton;
    private UserProfile profile;
    private User currentUser;

    public ProfileCustomizationGUI(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Customize Profile - " + currentUser.getUsername());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Load existing profile or create new one
        profile = loadOrCreateProfile();

        // --- Left panel: Profile picture ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        if (!profile.getPhotoPath().isEmpty()) {
            ImageIcon icon = new ImageIcon(profile.getPhotoPath());
            Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(scaled));
        } else {
            photoLabel.setText("No photo selected");
        }

        uploadButton = new JButton("Upload Photo");
        JPanel uploadPanel = new JPanel();
        uploadPanel.add(uploadButton);

        leftPanel.add(photoLabel, BorderLayout.CENTER);
        leftPanel.add(uploadPanel, BorderLayout.SOUTH);

        // --- Right panel: Bio + Save ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        bioTextArea = new JTextArea(profile.getBio().isEmpty() ? "Write your bio here..." : profile.getBio());
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane bioScroll = new JScrollPane(bioTextArea);
        bioScroll.setBorder(BorderFactory.createTitledBorder("Your Bio"));

        saveButton = new JButton("Save Profile");

        rightPanel.add(bioScroll, BorderLayout.CENTER);
        rightPanel.add(saveButton, BorderLayout.SOUTH);

        // --- Add both panels side by side ---
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // --- Button Logic ---
        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                profile.setPhotoPath(file.getAbsolutePath());
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        });

        saveButton.addActionListener(e -> saveProfile());

        setVisible(true);
    }

    private UserProfile loadOrCreateProfile() {
        String filename = "profiles/" + currentUser.getUsername() + "_profile.ser";
        try {
            return UserProfile.loadProfile(filename);
        } catch (Exception e) {
            System.out.println("No saved profile found for " + currentUser.getUsername() + ", creating new one.");
            return new UserProfile();
        }
    }

    private void saveProfile() {
        try {
            profile.setBio(bioTextArea.getText());
            String filename = "profiles/" + currentUser.getUsername() + "_profile.ser";
            profile.saveProfile(filename);
            JOptionPane.showMessageDialog(this, "Profile saved successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile.");
        }
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileCustomizationGUI(new User("jdoe")));
    }
}