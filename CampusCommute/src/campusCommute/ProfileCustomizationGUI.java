package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProfileCustomizationGUI extends JFrame {

    private JTextArea bioTextArea;
    private JLabel photoLabel;
    private JButton uploadButton, saveButton, emergencyContactButton;

    // --- MODIFIED: Added fields for US15 ---
    private JCheckBox confirmToggle;
    private JCheckBox cancelToggle;
    private JCheckBox reminderToggle;

    private Person currentUser;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ProfileCustomizationGUI(Person currentUser) {
        this.currentUser = currentUser;

        setTitle("Customize Profile - " + currentUser.getEmail());
        setSize(600, 500); // --- MODIFIED: Increased height for new panel
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Left panel - photo
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Load existing photo
        if (currentUser.getPhotoPath() != null && !currentUser.getPhotoPath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(currentUser.getPhotoPath());
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                photoLabel.setText("Photo not found");
                System.err.println("Could not load image: " + e.getMessage());
            }
        } else {
            photoLabel.setText("No photo selected");
        }

        uploadButton = new JButton("Upload Photo");
        leftPanel.add(photoLabel, BorderLayout.CENTER);
        leftPanel.add(uploadButton, BorderLayout.SOUTH);

        // --- MODIFIED: Right panel now includes bio AND notifications ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 15)); // Added vertical gap
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        // Bio Panel
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioTextArea = new JTextArea();
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        JScrollPane scrollPane = new JScrollPane(bioTextArea);
        bioPanel.add(new JLabel("Your Bio:"), BorderLayout.NORTH);
        bioPanel.add(scrollPane, BorderLayout.CENTER);

        // Notification Panel
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Notification Preferences"));

        confirmToggle = new JCheckBox("Notify on ride confirmations");
        cancelToggle = new JCheckBox("Notify on ride cancellations");
        reminderToggle = new JCheckBox("Notify with ride reminders");

        // Set state from user object
        confirmToggle.setSelected(currentUser.isNotifyOnConfirmation());
        cancelToggle.setSelected(currentUser.isNotifyOnCancellation());
        reminderToggle.setSelected(currentUser.isNotifyOnReminder());

        notificationPanel.add(confirmToggle);
        notificationPanel.add(cancelToggle);
        notificationPanel.add(reminderToggle);

        // Add both panels to the right panel
        rightPanel.add(bioPanel, BorderLayout.CENTER);
        rightPanel.add(notificationPanel, BorderLayout.SOUTH);
        // --- END OF MODIFICATION ---

        // Bottom - buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        emergencyContactButton = new JButton("Update Emergency Contact");
        saveButton = new JButton("Save Profile");

        buttonPanel.add(emergencyContactButton);
        buttonPanel.add(saveButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button handlers
        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                currentUser.setPhotoPath(file.getAbsolutePath());

                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        });

        saveButton.addActionListener(e -> saveProfile());

        emergencyContactButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Coming soon!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE)
        );

        setVisible(true);
    }

    private void saveProfile() {
        try {
            // --- MODIFIED: Save all fields ---
            currentUser.setBio(bioTextArea.getText());
            // photoPath is already set by its listener
            currentUser.setNotifyOnConfirmation(confirmToggle.isSelected());
            currentUser.setNotifyOnCancellation(cancelToggle.isSelected());
            currentUser.setNotifyOnReminder(reminderToggle.isSelected());
            // --- END OF MODIFICATION ---

            String jsonBody = gson.toJson(currentUser);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/profile/update"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                CurrentUser.loggedInUser = gson.fromJson(response.body(), Person.class);

                JOptionPane.showMessageDialog(this,
                        "Profile saved successfully!");
                
                // This is your new line, I've kept it
                new ViewProfileGUI(CurrentUser.loggedInUser); 

                dispose(); // This closes the edit window
            } else {
                JOptionPane.showMessageDialog(this,
                        "Server error " + response.statusCode(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not connect: " + ex.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}