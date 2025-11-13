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

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(loginButton);
        btnPanel.add(signupButton);
        form.add(btnPanel, gcBtn);

        //login button goes to the method that validates login
        loginButton.addActionListener(e -> validateAndMaybeLogin());
        
        //signup button goes to to the signup opening method
        signupButton.addActionListener(e -> openSignupPage());

        setContentPane(form);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(400, getHeight()));
        setLocationRelativeTo(null);
    }
    /**
     * Helper method to validate login information
     */
    private void validateAndMaybeLogin() {
        // --- 1. Your original validation ---
        emailField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        passwordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            if (email.isEmpty()) emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
            if (pass.isEmpty()) passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. The new networking part ---
        try {
            // 1. Create the LoginRequest object
            LoginRequest loginData = new LoginRequest(email, pass);

            // 2. Convert it to JSON
            Gson gson = new Gson();
            String jsonRequestBody = gson.toJson(loginData);

            // 3. Create the client and request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/login")) // The new login endpoint
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            // 4. Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Check the response code!
            if (response.statusCode() == 200) {
                // 200 = OK (Success!)
            	
            	Person userFromDb = gson.fromJson(response.body(), Person.class);
            	CurrentUser.loggedInUser = userFromDb;
            	
                JOptionPane.showMessageDialog(this,
                        "Login successful!",
                        "Welcome",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if (response.statusCode() == 401) {
                // 401 = Unauthorized (Wrong password/email)
                JOptionPane.showMessageDialog(this,
                    "Incorrect email or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                // Other server error
                JOptionPane.showMessageDialog(this,
                    "Server error: " + response.statusCode(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error connecting to server: " + ex.getMessage() +
                "\n\n(Is the Springboot server running?)",
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
