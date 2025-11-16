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

    // --- NEW FIELDS FOR US15 ---
    private JCheckBox confirmToggle;
    private JCheckBox cancelToggle;
    private JCheckBox reminderToggle;

    private Person currentUser;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ProfileCustomizationGUI(Person currentUser) {
        this.currentUser = currentUser;
        
        setTitle("Customize Profile - " + currentUser.getEmail());
        setSize(600, 500); // Increased height for notification panel
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); 

        // Left panel: Profile picture
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));

        photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(200, 200));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Load data from 'currentUser'
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

        // RIGHT PANEL — BIO + NOTIFICATIONS
        JPanel rightPanel = new JPanel(new BorderLayout(10, 15)); // Added vertical gap
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        // Create the bioPanel to hold the bio components
        JPanel bioPanel = new JPanel(new BorderLayout());
        bioTextArea = new JTextArea();
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        bioTextArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        
        JScrollPane scrollPane = new JScrollPane(bioTextArea);
        
        bioPanel.add(new JLabel("Your Bio:"), BorderLayout.NORTH);
        bioPanel.add(scrollPane, BorderLayout.CENTER);
        
        // --- NEW NOTIFICATION PANEL (US15) ---
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setBorder(BorderFactory.createTitledBorder("Notification Preferences"));

        confirmToggle = new JCheckBox("Notify on ride confirmations");
        cancelToggle = new JCheckBox("Notify on ride cancellations");
        reminderToggle = new JCheckBox("Notify with ride reminders");

        // Set state from the user's profile
        confirmToggle.setSelected(currentUser.isNotifyOnConfirmation());
        cancelToggle.setSelected(currentUser.isNotifyOnCancellation());
        reminderToggle.setSelected(currentUser.isNotifyOnReminder());

        notificationPanel.add(confirmToggle);
        notificationPanel.add(cancelToggle);
        notificationPanel.add(reminderToggle);
        // --- END OF NEW PANEL ---

        // Add the panels to the rightPanel
        rightPanel.add(bioPanel, BorderLayout.CENTER);
        rightPanel.add(notificationPanel, BorderLayout.SOUTH); // Add new panel here


        // Bottom panel: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        emergencyContactButton = new JButton("Update Emergency Contact");
        buttonPanel.add(emergencyContactButton);
        
        saveButton = new JButton("Save Profile");
        buttonPanel.add(saveButton);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                currentUser.setPhotoPath(file.getAbsolutePath()); 
                
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        });

        saveButton.addActionListener(e -> saveProfile());
        
        emergencyContactButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "This feature is coming soon!", 
                "Feature Not Implemented", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        setVisible(true);
    }

    /**
     * Saves the current profile (bio, photo, and notifications) to the backend.
     */
    private void saveProfile() {
        try {
            // 1. Update the local currentUser object from the GUI
            currentUser.setBio(bioTextArea.getText());
            // (photoPath is already set by its listener)

            // --- NEW LOGIC FOR US15 ---
            currentUser.setNotifyOnConfirmation(confirmToggle.isSelected());
            currentUser.setNotifyOnCancellation(cancelToggle.isSelected());
            currentUser.setNotifyOnReminder(reminderToggle.isSelected());
            // --- END OF NEW LOGIC ---

            // 2. Convert the currentUser object to a JSON string
            String jsonBody = gson.toJson(currentUser);

            // 3. Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/profile/update"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            // 4. Send the request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Handle the response
            if (response.statusCode() == 200) {
                // Update the global user with the fresh data from the server
                CurrentUser.loggedInUser = gson.fromJson(response.body(), Person.class);
                
                JOptionPane.showMessageDialog(this, "Profile saved successfully!");
                dispose(); // Close the profile window
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error saving profile. Server responded with: " + response.statusCode(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                        "Error connecting to server: " + ex.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method for testing this GUI frame independently.
     */
    public static void main(String[] args) {
        // Create a mock person for testing
        Person testPerson = new Person("test@user.com", "123-456-7890", 
            "Test", "User", "2000-01-01", "password", "123 Main St", "user");
        
        testPerson.setBio("This is a test bio.");
        CurrentUser.loggedInUser = testPerson;

        // Run the GUI
        SwingUtilities.invokeLater(() -> new ProfileCustomizationGUI(CurrentUser.loggedInUser));
    }
}