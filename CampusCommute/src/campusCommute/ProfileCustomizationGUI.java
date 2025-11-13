package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProfileCustomizationGUI extends JFrame {
    private JTextArea bioTextArea;
    private JLabel photoLabel;
    private JButton uploadButton, saveButton;
    
    // --- NEW BUTTON ---
    private JButton emergencyContactButton; 
    
    // --- THIS IS THE LOGGED-IN USER ---
    private Person currentUser;
    
    // --- HTTP CLIENT FOR NETWORKING ---
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    /**
     * Constructor now takes the logged-in Person object.
     * @param currentUser The user who is currently logged in.
     */
    public ProfileCustomizationGUI(Person currentUser) {
        this.currentUser = currentUser;
        
        setTitle("Customize Profile - " + currentUser.getEmail());
        setSize(600, 450); // Increased height for the new button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center the window

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

        // Right panel: Bio
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        bioTextArea = new JTextArea();
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        
        // Load data frm 'currentUser'
        bioTextArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        
        JScrollPane scrollPane = new JScrollPane(bioTextArea);
        rightPanel.add(new JLabel("Your Bio:"), BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

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


        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                // Update 'currentUser' OBJECT (in memory)
                currentUser.setPhotoPath(file.getAbsolutePath()); 
                
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaled));
                photoLabel.setText("");
            }
        });

        // New 'saveProfile()' method is called
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
     * Saves the current profile (bio and photo path) to the backend database
     * by sending an HTTP POST request to the /api/profile/update endpoint.
     */
    private void saveProfile() {
        try {
            // 1. Update the local currentUser object from the GUI
            currentUser.setBio(bioTextArea.getText());
            // (The photo path was already set by its own listener)

            // 2. Convert the currentUser object to a JSON string
            String jsonBody = gson.toJson(currentUser);

            // 3. Create the HTTP request to send to the server
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/profile/update"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            // 4. Send the request and get the response
            // We use .send() here (blocking) because it's a simple save operation.
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Handle the server's response
            if (response.statusCode() == 200) {
                // SUCCESS!
                // The server sends back the updated person. We update our global user.
                CurrentUser.loggedInUser = gson.fromJson(response.body(), Person.class);
                
                JOptionPane.showMessageDialog(this, "Profile saved successfully!");
                dispose(); // Close the profile window
            } else {
                // FAILURE
                JOptionPane.showMessageDialog(this,
                        "Error saving profile. Server responded with: " + response.statusCode(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            // This catches connection errors (e.g., server is off)
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
        // In your real application, you would open this GUI from another
        // window (like a main menu) and pass 'CurrentUser.loggedInUser'
        // into the constructor.
        
        // Create a mock person for testing this window
        Person testPerson = new Person("test@user.com", "123-456-7890", 
            "Test", "User", "2000-01-01", "password", "123 Main St", "user");
        
        // Set some test data
        testPerson.setBio("This is a test bio that should load into the text area.");
        // testPerson.setPhotoPath("C:/path/to/some/image.png"); // (Optional)
        
        // This is the global user for the app
        CurrentUser.loggedInUser = testPerson;

        // Run the GUI
        SwingUtilities.invokeLater(() -> new ProfileCustomizationGUI(CurrentUser.loggedInUser));
    }
}