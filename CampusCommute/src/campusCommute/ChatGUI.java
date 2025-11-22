package campusCommute;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;

public class ChatGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    
    private Long rideId;
    private String currentUserName;
    private Long currentUserId;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Timer pollingTimer; // Automatically refreshes chat

    public ChatGUI(Long rideId) {
        this.rideId = rideId;
        this.currentUserId = CurrentUser.loggedInUser.getID();
        this.currentUserName = CurrentUser.loggedInUser.getfName();

        setTitle("Ride Chat (Ride #" + rideId + ")");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Chat History Area ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // --- Input Panel ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputField = new JTextField();
        sendButton = new JButton("Send");
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(inputPanel, BorderLayout.SOUTH);

        // --- Actions ---
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage()); // Allow 'Enter' key to send

        // --- Start Polling (Refresh every 2 seconds) ---
        startChatPolling();
        
        setVisible(true);
    }

    private void sendMessage() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;

        try {
            // Create message object
            Message msg = new Message(rideId, currentUserId, currentUserName, content);
            String json = gson.toJson(msg);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/chat/send"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            inputField.setText(""); // Clear input
            loadMessages(); // Refresh immediately

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMessages() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/chat/" + rideId))
                    .GET()
                    .build();

            HttpResponse<String> response = 
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            Message[] messages = gson.fromJson(response.body(), Message[].class);

            StringBuilder sb = new StringBuilder();
            if (messages != null) {
                for (Message m : messages) {
                    // Format: [Name]: Message content
                    sb.append("[").append(m.getSenderName()).append("]: ")
                      .append(m.getContent()).append("\n");
                }
            }
            
            // Only update text if it changed to prevent flickering
            if (!chatArea.getText().equals(sb.toString())) {
                chatArea.setText(sb.toString());
                // Scroll to bottom
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }

        } catch (Exception e) {
            // Ignore polling errors to avoid popup spam
            System.out.println("Chat poll error: " + e.getMessage());
        }
    }

    private void startChatPolling() {
        // Timer calls loadMessages() every 2000ms (2 seconds)
        pollingTimer = new Timer(2000, e -> loadMessages());
        pollingTimer.start();
    }

    @Override
    public void dispose() {
        if (pollingTimer != null) pollingTimer.stop(); // Stop timer when window closes
        super.dispose();
    }
    
    // --- Local Message Class for JSON (matches backend) ---
    static class Message {
        Long rideId, senderId;
        String senderName, content;
        
        // --- FIX: Added No-Args Constructor for Gson ---
        public Message() {} 
        // -----------------------------------------------

        public Message(Long rideId, Long senderId, String senderName, String content) {
            this.rideId = rideId; this.senderId = senderId;
            this.senderName = senderName; this.content = content;
        }
        public String getSenderName() { return senderName; }
        public String getContent() { return content; }
    }
}