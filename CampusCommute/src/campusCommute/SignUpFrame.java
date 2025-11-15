package campusCommute;

// Add these lines with your other imports
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Frame for the signup window, uses person and datamanager class
 * --- NOW WITH AUTO-LOGIN to profile customization ---
 */
public class SignUpFrame extends JFrame {
    // creates text fields for signup fields
    private final JTextField emailField = new JTextField(20);
    private final JTextField fNameField = new JTextField(20);
    private final JTextField lNameField = new JTextField(20);
    private final JTextField dobField = new JTextField(10); // YYYY-MM-DD
    private final JPasswordField passField = new JPasswordField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField adressField = new JTextField(25);
    private final JComboBox<String> userTypeBox = new JComboBox<>(new String[]{"driver", "user", "both"});

    private final JButton signUpBtn = new JButton("Sign Up");

    // --- FIX: Declared as class fields ---
    // Both handleSignUp and performAutoLogin can share these
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public SignUpFrame() {
        super("CampusCommute — Sign Up");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints base = new GridBagConstraints();
        base.insets = new Insets(6, 10, 6, 10);
        base.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        //add the detail inputting fields using helper from below
        addRow(form, base, row++, "Email:", emailField);
        addRow(form, base, row++, "First Name:", fNameField);
        addRow(form, base, row++, "Last Name:", lNameField);
        addRow(form, base, row++, "Date of Birth (YYYY-MM-DD):", dobField);
        addRow(form, base, row++, "Password:", passField);
        addRow(form, base, row++, "Phone (max 11 chars):", phoneField);
        addRow(form, base, row++, "Address:", adressField);
        addRow(form, base, row++, "User Type:", userTypeBox);

        //signup button
        GridBagConstraints gcBtn = (GridBagConstraints) base.clone();
        gcBtn.gridx = 0; gcBtn.gridy = row; gcBtn.gridwidth = 2;
        gcBtn.weightx = 1.0; gcBtn.anchor = GridBagConstraints.EAST;

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.add(signUpBtn);
        form.add(btnPanel, gcBtn);

        //used for validation on click, starts the check on the entered data using handleSignupMethod
        signUpBtn.addActionListener(this::handleSignUp);

        setContentPane(wrapWithPadding(form, 12));
        // --- FIX ---
        // Was EXIT_ON_CLOSE, which kills the app. Should be DISPOSE_ON_CLOSE.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(460, getHeight()));
        setLocationRelativeTo(null);
    }


    private void handleSignUp(ActionEvent evt) {
        // --- 1. This is all your original validation code ---
        String email = emailField.getText().trim();
        String fName = fNameField.getText().trim();
        String lName = lNameField.getText().trim();
        String dob = dobField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String phone = phoneField.getText().trim();
        String adress = adressField.getText().trim();
        String userType = (String) userTypeBox.getSelectedItem();

        if (email.isEmpty() || fName.isEmpty() || lName.isEmpty()
                || dob.isEmpty() || pass.isEmpty() || phone.isEmpty()
                || adress.isEmpty() || userType == null || userType.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You must fill all the details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ... (other validation checks) ...
        if (!isValidDateYYYYMMDD(dob)) {
            JOptionPane.showMessageDialog(this,
                    "DOB must be in the format YYYY-MM-DD (e.g., 2001-02-28).",
                    "Invalid Date of Birth",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!phone.matches("\\d{1,11}")) {
            JOptionPane.showMessageDialog(this,
                    "Phone must contain only digits (max 11).",
                    "Invalid Phone",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        // --- 2. This is the new networking part ---
        try {
            // Create the Person object just like before
            Person p = new Person(
                    email, phone, fName, lName, dob, pass, adress, userType
            );

            // --- FIX: Use the class field 'gson'. Do not declare a new one. ---
            String jsonRequestBody = gson.toJson(p);
            
            // --- FIX: Do not declare 'client' here. We will use the class field 'httpClient'. ---
            
            // Create a request to send to your server
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/signup")) // Your server's API endpoint
                    .header("Content-Type", "application/json") // Tell the server we're sending JSON
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody)) // Send the JSON data
                    .build();

            // --- FIX: Use the class field 'httpClient'. ---
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // --- 3. MODIFIED SUCCESS BLOCK ---
            if (response.statusCode() == 200 || response.statusCode() == 201) { // 201 is also "Created"
                JOptionPane.showMessageDialog(this,
                        "Sign-up successful! Logging you in to customize your profile...",
                        "Account Created",
                        JOptionPane.INFORMATION_MESSAGE);

                // NOW, AUTOMATICALLY LOG THEM IN to get the new Person object
                performAutoLogin(email, pass);

            } else {
                // Show an error if the server failed
                JOptionPane.showMessageDialog(this,
                        "Sign-up failed. Server responded with: " + response.statusCode(),
                        "Server Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            // This catches network errors (like if the server is off)
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error during sign-up: " + ex.getMessage() +
                            "\n\n(Did you remember to run the Springboot server?)",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * --- NEW METHOD ---
     * Performs an automatic login after signup to fetch the new user's
     * full Person object (including their ID).
     */
    private void performAutoLogin(String email, String password) {
        try {
            // This assumes you have the LoginRequest.java file in your project
            LoginRequest body = new LoginRequest(email, password);
            
            // --- FIX: Use the class field 'gson'. ---
            String jsonBody = gson.toJson(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("Content-Type", "application/json")
                    .build();

            // --- FIX: Use the class field 'httpClient'. ---
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Success! We got the new Person object
                // --- FIX: Use the class field 'gson'. ---
                Person loggedIn = gson.fromJson(response.body(), Person.class);
                
                // CRITICAL: Set the global user
                CurrentUser.loggedInUser = loggedIn; 

                // Close this signup frame
                dispose(); 
                
                // Open the Profile Customization GUI
                new ProfileCustomizationGUI(loggedIn).setVisible(true);

            } else {
                // This is a weird case (signup worked but login failed)
                // Just send them to the login frame to be safe.
                JOptionPane.showMessageDialog(this, "Account created, but auto-login failed. Please log in manually.");
                dispose();
                new LoginFrame().setVisible(true);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during auto-login: " + ex.getMessage());
            // Send to login frame
            dispose();
            new LoginFrame().setVisible(true);
        }
    }


    /**
     * private helper method to validate if a given string is a valid date format
     */
    private boolean isValidDateYYYYMMDD(String s) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);
            LocalDate.parse(s, fmt);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * This method is used to shorten code when creating fields
     */
    private static void addRow(JPanel panel, GridBagConstraints base, int row, String label, JComponent field) {
        GridBagConstraints gcLabel = (GridBagConstraints) base.clone();
        gcLabel.gridx = 0; gcLabel.gridy = row; gcLabel.weightx = 0; gcLabel.gridwidth = 1;
        panel.add(new JLabel(label), gcLabel);

        GridBagConstraints gcField = (GridBagConstraints) base.clone();
        gcField.gridx = 1; gcField.gridy = row; gcField.weightx = 1.0; gcField.gridwidth = 1;
        panel.add(field, gcField);
    }

    /**
     * padding method
     */
    private static JComponent wrapWithPadding(JComponent inner, int pad) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpFrame().setVisible(true));
    }
}