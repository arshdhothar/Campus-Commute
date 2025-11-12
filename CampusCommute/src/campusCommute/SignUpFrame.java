package campusCommute;

//Add these lines with your other imports
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
 */
public class SignUpFrame extends JFrame {
//creates text fields for signup fields
    private final JTextField emailField   = new JTextField(20);
    private final JTextField fNameField   = new JTextField(20);
    private final JTextField lNameField   = new JTextField(20);
    private final JTextField dobField     = new JTextField(10); // YYYY-MM-DD
    private final JPasswordField passField= new JPasswordField(20);
    private final JTextField phoneField   = new JTextField(15);
    private final JTextField adressField  = new JTextField(25);
    private final JComboBox<String> userTypeBox = new JComboBox<>(new String[] {"driver", "user", "both"});

    private final JButton signUpBtn = new JButton("Sign Up");

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(460, getHeight()));
        setLocationRelativeTo(null);
    }

    
    private void handleSignUp(ActionEvent evt) {
        // --- 1. This is all your original validation code ---
        // (It's good code, we're keeping it)
        String email   = emailField.getText().trim();
        String fName   = fNameField.getText().trim();
        String lName   = lNameField.getText().trim();
        String dob     = dobField.getText().trim();
        String pass    = new String(passField.getPassword()).trim();
        String phone   = phoneField.getText().trim();
        String adress  = adressField.getText().trim();
        String userType= (String) userTypeBox.getSelectedItem();

        if (email.isEmpty() || fName.isEmpty() || lName.isEmpty()
                || dob.isEmpty() || pass.isEmpty() || phone.isEmpty()
                || adress.isEmpty() || userType == null || userType.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You must fill all the details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add your other validation checks here too (like for date and phone)
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
            // NOTE: Make sure your Person.java in this GUI project still has
            // this constructor.
            Person p = new Person(
                email, phone, fName, lName, dob, pass, adress, userType
            );
            
            // This is the new code that replaces UserDataManager.addUser(p)

            // 1. Convert the Person object to a JSON string
            Gson gson = new Gson();
            String jsonRequestBody = gson.toJson(p);

            // 2. Create an HTTP client (the "phone")
            HttpClient client = HttpClient.newHttpClient();

            // 3. Create a request to send to your server (the "order")
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/signup")) // Your server's API endpoint
                    .header("Content-Type", "application/json") // Tell the server we're sending JSON
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody)) // Send the JSON data
                    .build();

            // 4. Send the request and get the response
            // We're using send() for simplicity. It will "freeze" the GUI
            // for a split second, which is fine for this project.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Check if the server said "OK" (HTTP code 200)
            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this,
                    "Sign-up successful! You can now log in.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Open the login window
                SwingUtilities.invokeLater(() -> {
                    dispose(); // closes this SignUpFrame
                    new LoginFrame().setVisible(true); // opens login window
                });
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
     * private helper method to validate if a given string is a valid date format
     * @param s The string to check
     * @return true if in correct format false otherwise
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
     * This method is used to shorten code when creating fields, it creates a field at the given field
     * @param panel the panel we are using
     * @param base base of the panel
     * @param row the row number of the field
     * @param label a label for the field
     * @param field component object
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
     * padding method not used currently, might need later
     * @param inner
     * @param pad
     * @return
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
