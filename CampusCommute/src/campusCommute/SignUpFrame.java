package campusCommute;

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
/**
 * This method handles all the formatting checkings done for signup
 * @param evt
 */
    private void handleSignUp(ActionEvent evt) {
        //Trim the strings for safe format checking
        String email   = emailField.getText().trim();
        String fName   = fNameField.getText().trim();
        String lName   = lNameField.getText().trim();
        String dob     = dobField.getText().trim();
        String pass    = new String(passField.getPassword()).trim();
        String phone   = phoneField.getText().trim();
        String adress  = adressField.getText().trim();
        String userType= (String) userTypeBox.getSelectedItem();

        //This block checkks if any field is left empty, if uses pop out message to warn the user
        if (email.isEmpty() || fName.isEmpty() || lName.isEmpty()
                || dob.isEmpty() || pass.isEmpty() || phone.isEmpty()
                || adress.isEmpty() || userType == null || userType.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You must fill all the details.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        //this block validates the format of the Date of birth, ensures its in YYYY-MM-DD format, displays error if not
        //uses helper method isValidDate from below
        if (!isValidDateYYYYMMDD(dob)) {
            JOptionPane.showMessageDialog(this,
                    "DOB must be in the format YYYY-MM-DD (e.g., 2001-02-28).",
                    "Invalid Date of Birth",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //checks if the phone number is less than 11 digits
        if (phone.length() > 11) {
            JOptionPane.showMessageDialog(this,
                    "Phone must not be longer than 11 characters.",
                    "Invalid Phone",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //checks if the phone number is made out of ints only, no strings allowed
        if (!phone.matches("\\d{1,11}")) {
            JOptionPane.showMessageDialog(this,
                    "Phone must contain only digits (max 11).",
                    "Invalid Phone",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //after the checks tries to create a person object using all the input data
        try {
            Person p = new Person(
                email,
                phone,
                fName,
                lName,
                dob,
                pass,
                adress,
                userType
            );
            //calls static method from user data manager, to append the user to the json file
            UserDataManager.addUser(p);
//sucess message
            JOptionPane.showMessageDialog(this,
                "Sign-up successful! You can now log in.",
                "Account Created",
                JOptionPane.INFORMATION_MESSAGE);
//on sucess close this window, and open a login window
            SwingUtilities.invokeLater(() -> {
                dispose(); // closes this SignUpFrame
                new LoginFrame().setVisible(true); // opens login window
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error saving user: " + ex.getMessage(),
                "File Error",
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
