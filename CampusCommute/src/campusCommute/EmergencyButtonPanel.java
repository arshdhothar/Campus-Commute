package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EmergencyButtonPanel extends JPanel {
    private final String userName;
    private final String userPhone;
    private String rideId;

    public EmergencyButtonPanel(String userName, String userPhone, String rideId) {
        this.userName = userName;
        this.userPhone = userPhone;
        this.rideId = rideId;

        JButton emergencyBtn = new JButton("EMERGENCY");
        emergencyBtn.setBackground(Color.RED);
        emergencyBtn.setForeground(Color.WHITE);
        emergencyBtn.setFont(new Font("Arial", Font.BOLD, 18));
        emergencyBtn.addActionListener(this::sendEmergency);

        setLayout(new FlowLayout(FlowLayout.RIGHT));
        add(emergencyBtn);
    }

    private void sendEmergency(ActionEvent e) {
        boolean ok = EmergencyAlertRequest.send(userName, userPhone, rideId, 0.0, 0.0);
        JOptionPane.showMessageDialog(this,
                ok ? "Emergency alert sent." : "Failed to send alert.");
    }
    public void setRideId(String rideId) {
        this.rideId = rideId;
    }
}