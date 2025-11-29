package campusCommute;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.google.gson.Gson;

public class LostAndFoundPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    private JTextField txtDriverDetails; 
    private JTextArea txtDescription;
    private JRadioButton rbPhone;
    private JRadioButton rbEmail;
    private ButtonGroup contactGroup;

    public LostAndFoundPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // --- 1. Header ---
        JLabel lblHeader = new JLabel("Report Lost Item", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblHeader.setForeground(new Color(50, 50, 50));
        lblHeader.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- 2. Center Wrapper ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        add(centerWrapper, BorderLayout.CENTER);

        // --- 3. The Form Card ---
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // -- Description Label --
        JLabel lblDesc = new JLabel("Item Description");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDesc.setForeground(new Color(80, 80, 80));
        gbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(lblDesc, gbc);

        // -- Description Text Area --
        gbc.gridy++;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        scrollPane.setPreferredSize(new Dimension(400, 100));
        
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(scrollPane, gbc);

        // -- Driver/Ride Details Label --
        gbc.gridy++;
        JLabel lblDriver = new JLabel("Associated Driver / Ride Details");
        lblDriver.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDriver.setForeground(new Color(80, 80, 80));
        gbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(lblDriver, gbc);

        // -- Driver Details Field --
        gbc.gridy++;
        txtDriverDetails = new JTextField();
        txtDriverDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDriverDetails.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5))
        );
        txtDriverDetails.setPreferredSize(new Dimension(400, 35));
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(txtDriverDetails, gbc);

        // --- PREFERRED CONTACT SELECTOR ---
        gbc.gridy++;
        JLabel lblContact = new JLabel("Preferred Contact Method:");
        lblContact.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContact.setForeground(new Color(80, 80, 80));
        gbc.insets = new Insets(0, 0, 8, 0);
        formCard.add(lblContact, gbc);

        // Get current user info for labels
        String phone = (CurrentUser.loggedInUser != null) ? CurrentUser.loggedInUser.getPhone() : "N/A";
        String email = (CurrentUser.loggedInUser != null) ? CurrentUser.loggedInUser.getEmail() : "N/A";

        rbPhone = new JRadioButton("Phone (" + phone + ")");
        rbPhone.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbPhone.setBackground(Color.WHITE);
        rbPhone.setSelected(true); // Default to phone

        rbEmail = new JRadioButton("Email (" + email + ")");
        rbEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbEmail.setBackground(Color.WHITE);

        // Group 
        contactGroup = new ButtonGroup();
        contactGroup.add(rbPhone);
        contactGroup.add(rbEmail);

        // Add to panel
        gbc.gridy++;
        formCard.add(rbPhone, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 30, 0); // Spacing before button
        formCard.add(rbEmail, gbc);
        // ----------------------------------------

        // -- Submit Button --
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JButton btnSubmit = new JButton("Submit Report");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSubmit.setBackground(new Color(30, 144, 255));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(200, 45));
        
        btnSubmit.addActionListener(e -> handleSubmitReport());
        
        formCard.add(btnSubmit, gbc);
        centerWrapper.add(formCard);
    }

    private void handleSubmitReport() {
        String description = txtDescription.getText().trim();
        String driverInfo = txtDriverDetails.getText().trim();

        if (description.isEmpty() || driverInfo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Get basic user info
            String selectedContact = "Unknown";
            

            String userEmail = CurrentUser.loggedInUser.getEmail();
                
                // --- CHOOSE CONTACT INFO ---
            if (rbPhone.isSelected()) {
                selectedContact = CurrentUser.loggedInUser.getPhone() + " (Phone)";
            } else {
                 selectedContact = CurrentUser.loggedInUser.getEmail() + " (Email)";
            }
            
            
            // Create report with the SELECTED contact info
            LostItemReport report = new LostItemReport(userEmail, description, selectedContact, driverInfo);

            Gson gson = new Gson();
            String jsonBody = gson.toJson(report);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/api/reportLostItem"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JOptionPane.showMessageDialog(this, "Report Submitted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtDescription.setText("");
                txtDriverDetails.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Submission Failed: " + response.statusCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}