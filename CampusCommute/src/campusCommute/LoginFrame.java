package campusCommute;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame
 * Handles user login and routing to the correct dashboard.
 */
public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    private final JButton loginButton = new JButton("Login");
    private final JButton signupButton = new JButton("Go to Sign Up");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public LoginFrame() {
        super("CampusCommute — Login");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints base = new GridBagConstraints();
        base.insets = new Insets(8, 12, 8, 12);
        base.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, base, row++, "Email:", emailField);
        addRow(form, base, row++, "Password:", passwordField);

        GridBagConstraints gcBtn = (GridBagConstraints) base.clone();
        gcBtn.gridx = 0; gcBtn.gridy = row; gcBtn.gridwidth = 2;
        gcBtn.anchor = GridBagConstraints.CENTER;

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(loginButton);
        buttons.add(signupButton);
        form.add(buttons, gcBtn);

        loginButton.addActionListener(e -> performLogin());
        signupButton.addActionListener(e -> openSignupPage());

        setContentPane(form);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // Handles the login process
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password required.");
            return;
        }

        LoginRequest body = new LoginRequest(email, password);
        String jsonBody = gson.toJson(body);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                JOptionPane.showMessageDialog(this, "Invalid login.");
                return;
            }

            // Parse the user and save to session
            Person loggedIn = gson.fromJson(response.body(), Person.class);
            CurrentUser.loggedInUser = loggedIn;

            JOptionPane.showMessageDialog(this, "Welcome " + loggedIn.getfName() + "!");

            // Route based on user type
            if (loggedIn.getUserType().equalsIgnoreCase("driver")) {
                showDriverOptions();
                dispose(); 

            } else if (loggedIn.getUserType().equalsIgnoreCase("user")) {
                // FIX: Make sure the window is visible before disposing this one
                new PassengerGUI().setVisible(true);
                dispose(); 

            } else {
                // "both" userType - ask them what they want to do
                Object choice = JOptionPane.showInputDialog(
                        this,
                        "Choose mode:",
                        "Select Mode",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Passenger", "Driver"},
                        "Passenger"
                );

                if ("Driver".equals(choice)) {
                    showDriverOptions();
                } else {
                    // FIX: Make visible
                    new PassengerGUI().setVisible(true);
                }
                dispose(); 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server error: " + ex.getMessage());
        }
    }

    // Shows options for drivers
    private void showDriverOptions() {
        String[] options = {"Manage My Rides", "Post a New Ride"};
        
        Object choice = JOptionPane.showInputDialog(
                null, 
                "What would you like to do?", 
                "Driver Menu", 
                JOptionPane.QUESTION_MESSAGE,
                null,
                options, 
                options[0] 
        );

        // FIX: Ensure these windows are also set to visible
        if ("Manage My Rides".equals(choice)) {
            new DriverGUI().setVisible(true);
        } else if ("Post a New Ride".equals(choice)) {
            new CampusCommuteGUI().setVisible(true);
        }
    }

    private static void addRow(JPanel panel, GridBagConstraints base, int row, String label, JComponent field) {
        GridBagConstraints gcLabel = (GridBagConstraints) base.clone();
        gcLabel.gridx = 0; gcLabel.gridy = row;
        panel.add(new JLabel(label), gcLabel);

        GridBagConstraints gcField = (GridBagConstraints) base.clone();
        gcField.gridx = 1; gcField.gridy = row;
        panel.add(field, gcField);
    }

    private void openSignupPage() {
        dispose();
        new SignUpFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}