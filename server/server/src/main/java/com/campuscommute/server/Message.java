package com.campuscommute.server;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;      // The ride this chat belongs to
    private Long senderId;    // Who sent it
    private String senderName; // Name to display in UI
    private String content;   // The actual text
    private String timestamp;

    public Message() {}

    public Message(Long rideId, Long senderId, String senderName, String content) {
        this.rideId = rideId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = LocalDateTime.now().toString();
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public Long getRideId() { return rideId; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    public void setRideId(Long rideId) { this.rideId = rideId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
 {
    
}

}