package campusCommute;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;

// --- ALL NEW IMPORTS FOR NETWORKING AND THE BUTTON ---
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
// ----------------------------------------------------


public class EmergencyContanctInfoFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtEmergencyName;
	private JTextField txtEmergencyPhone;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EmergencyContanctInfoFrame frame = new EmergencyContanctInfoFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EmergencyContanctInfoFrame() {
        // --- SECURITY CHECK (from US-3) ---
        // Make sure a user is actually logged in before showing this page
        if (CurrentUser.loggedInUser == null) {
            JOptionPane.showMessageDialog(null, "You must be logged in to view this page.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            // Close this frame immediately
            dispose();
            return; // Stop running the constructor
        }
        // ------------------------------------

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 728, 321);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblEmergencyName = new JLabel("Enter Emergency Contact Name:");
		lblEmergencyName.setBounds(144, 100, 197, 14);
		lblEmergencyName.setVerticalAlignment(SwingConstants.TOP);
		contentPane.add(lblEmergencyName);
		
		txtEmergencyName = new JTextField();
		txtEmergencyName.setBounds(351, 97, 203, 20);
		contentPane.add(txtEmergencyName);
		txtEmergencyName.setColumns(10);
		
		JLabel lblEmergencyPhone = new JLabel("Enter Emergency Contact Number:");
		lblEmergencyPhone.setBounds(144, 139, 203, 14);
		contentPane.add(lblEmergencyPhone);
		
		txtEmergencyPhone = new JTextField();
		txtEmergencyPhone.setBounds(351, 136, 203, 20);
		contentPane.add(txtEmergencyPhone);
		txtEmergencyPhone.setColumns(10);
		
		JButton btnSaveContact = new JButton("Update Info");
		btnSaveContact.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSaveContact.setBounds(144, 189, 410, 27);
		contentPane.add(btnSaveContact);
		
		JLabel lblPageHdr = new JLabel("Emergency Contact Info");
		lblPageHdr.setHorizontalAlignment(SwingConstants.CENTER);
		lblPageHdr.setFont(new Font("Tahoma", Font.PLAIN, 42));
		lblPageHdr.setBounds(124, 11, 445, 51);
		contentPane.add(lblPageHdr);
        
        // --- ADD ACTION LISTENER TO THE BUTTON ---
        btnSaveContact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call our new helper method
                handleUpdateContact();
            }
        });
	}
    
    /**
     * This method is called when the "Update Info" button is clicked.
     * It handles getting the data and sending it to the server.
     */
    private void handleUpdateContact() {
    	String name = txtEmergencyName.getText().trim();
    	String phone = txtEmergencyPhone.getText().trim();
    	
    	if(name.isEmpty() || phone.isEmpty()) {
    		JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
    	}
    	
    	if(phone.length() > 11) {
    		JOptionPane.showMessageDialog(this, "Phone must be less than 11 digits", "Incorrect Info", JOptionPane.WARNING_MESSAGE);
    	}
    	
    	String userEmail = CurrentUser.loggedInUser.getEmail();
    	
    	try {
    		EmergencyContactRequest req = new EmergencyContactRequest(userEmail, name, phone);
    		
    		Gson gson = new Gson();
    		String jsonBody = gson.toJson(req);
    		
    		HttpClient client = HttpClient.newHttpClient();
    		HttpRequest request = HttpRequest.newBuilder()
    				.uri(new URI("http://localhost:8080/api/updateEmergencyContact"))
    				.header("Content-Type", "application/json")
    				.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
    				.build();
    		
    		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    		
    		if(response.statusCode() == 200) {
    			JOptionPane.showMessageDialog(this, "Emergency Contact Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
    			
    			CurrentUser.loggedInUser.setEmergencyContactName(name);
                CurrentUser.loggedInUser.setEmergencyContactPhone(phone);
                
                dispose();
    		} else {
    			JOptionPane.showMessageDialog(this, "Update Failed: " + response.body(), "Server Error", JOptionPane.ERROR_MESSAGE);
    		}
    	} catch(Exception ex) {
    		ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }
}