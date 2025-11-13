package campusCommute;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;
import java.awt.*;

/**
 * This class handles the login part of the application, checks if the user is in the database before logging in, uses signup, and userdatamanager classes
 */
public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Login");
    private final JButton signupButton = new JButton("Go to Sign Up");

    // --- We need these for the network request ---
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public LoginFrame() {
    	//name window
        super("CampusCommute — Login");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints base = new GridBagConstraints();
        base.insets = new Insets(8, 12, 8, 12);
        base.fill = GridBagConstraints.HORIZONTAL;
        
        //uses the add row helper to add fields to the window
        int row = 0;
        addRow(form, base, row++, "Email:", emailField);
        addRow(form, base, row++, "Password:", passwordField);

        //row of the buttons, signup + login button
        GridBagConstraints gcBtn = (GridBagConstraints) base.clone();
        gcBtn.gridx = 0; gcBtn.gridy = row; gcBtn.gridwidth = 2;
        gcBtn.weightx = 1.0; gcBtn.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        form.add(buttonPanel, gcBtn);

        //add listeners
        loginButton.addActionListener(e -> performLogin());
        signupButton.addActionListener(e -> openSignupPage());

        setContentPane(form);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * This is the main login logic method.
     */
    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and Password are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 1. Create LoginRequest object and convert to JSON
        LoginRequest loginRequest = new LoginRequest(email, password);
        String jsonBody = gson.toJson(loginRequest);

        // 2. Build the HTTP Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        try {
            // 3. Send the request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Handle the response
            if (response.statusCode() == 200) {
                // SUCCESS!
                // Convert the JSON response (which is a full Person) into our Person object
                Person loggedInPerson = gson.fromJson(response.body(), Person.class);
                
                // Store this person as the globally logged-in user
                CurrentUser.loggedInUser = loggedInPerson;

                JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + loggedInPerson.getfName() + ".");
                dispose(); // Close the login window

                // --- THIS IS THE NEW CODE YOU ASKED FOR ---
                // After closing, open the ProfileCustomizationGUI
                // We pass the logged-in user to its constructor
                SwingUtilities.invokeLater(() -> {
                    new ProfileCustomizationGUI(CurrentUser.loggedInUser);
                });
                // ------------------------------------------

            } else if (response.statusCode() == 401) {
                // Unauthorized
                JOptionPane.showMessageDialog(this,
                    "Login Failed: Invalid email or password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                // Other server errors
                JOptionPane.showMessageDialog(this,
                    "Login Failed: Server error (Code: " + response.statusCode() + ")",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error connecting to server: " + ex.getMessage() +
                "\n\n(Is the Spring Boot server running?)",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * helper method to simplify adding rows to the window
     * @param panel active panel
     * @param base base
     * @param row the row number of the field
     * @param label the label we want for the field
     * @param field
     */
    private static void addRow(JPanel panel, GridBagConstraints base, int row, String label, JComponent field) {
        GridBagConstraints gcLabel = (GridBagConstraints) base.clone();
        gcLabel.gridx = 0; gcLabel.gridy = row; gcLabel.weightx = 0; gcLabel.gridwidth = 1;
        panel.add(new JLabel(label), gcLabel);

        GridBagConstraints gcField = (GridBagConstraints) base.clone();
        gcField.gridx = 1; gcField.gridy = row; gcField.weightx = 1.0; gcField.gridwidth = 1;
        panel.add(field, gcField);
    }
    
    //method to activate opening signup widnow
    private void openSignupPage() {
        SwingUtilities.invokeLater(() -> {
            dispose(); // close login window
            new SignUpFrame().setVisible(true); // open signup
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}