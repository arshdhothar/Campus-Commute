package campusCommute;

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
        
        //uses the addrow helper to add fields to the window
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
        //reset borders on call
        emailField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        passwordField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
        //trim inputs for formatting
        String email = emailField.getText().trim();
        String pass  = new String(passwordField.getPassword()).trim();
        //check if the input fields are empty
        boolean emailEmpty = email.isEmpty();
        boolean passEmpty  = pass.isEmpty();
        
        //checks for the fields
        if (emailEmpty || passEmpty) {
        	//if empty email higlihht email red
            if (emailEmpty) {
                emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
            //if password is empty highlight password red
            if (passEmpty) {
                passwordField.setBorder(BorderFactory.createLineBorder(Color.RED));
            }

            String msg;
            //msg to show to the user for the empty field
            if (emailEmpty && passEmpty) {
                msg = "Please enter your email and password.";
            } else if (emailEmpty) {
                msg = "Please enter your email.";
            } else {
                msg = "Please enter your password.";
            }
            //Sends popup window warning
            JOptionPane.showMessageDialog(this, msg, "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
        	//checks if the given inputs match the one in the database using the static method from userdatamanager
            boolean ok = UserDataManager.validateCredentials(email, pass);
            //if the check fails, dispplays an erorr message about wrong credentials
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Incorrect email or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            //on sucess
            
            //show sucess message
            JOptionPane.showMessageDialog(this,
                    "Login successful!",
                    "Welcome",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error reading users file: " + ex.getMessage(),
                    "File Error",
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
    //method to activate opening signup  widnow
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
