package com.campuscommute.server;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // This tells Spring to make a table called "lost_item_report"
public class LostItemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail; // Who reported it
    private String itemDescription;
    private String preferredContact; // Phone number or email
    private String driverInfo;
    private String status; // "Open", "Resolved", etc.

    // Default constructor (Required by JPA)
    public LostItemReport() {
    }

    // Constructor for creating a new report
    public LostItemReport(String userEmail, String itemDescription, String preferredContact, String driverInfo) {
        this.userEmail = userEmail;
        this.itemDescription = itemDescription;
        this.preferredContact = preferredContact;
        this.driverInfo = driverInfo;
        this.status = "Open"; // Default status
    }

    // --- Getters and Setters ---

    private String getDriverInfo() {
        return driverInfo;
    }

    private void setDriverInfo(String driverInfo) {
        this.driverInfo = driverInfo;
    }

    public Long getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getPreferredContact() {
        return preferredContact;
    }

    public void setPreferredContact(String preferredContact) {
        this.preferredContact = preferredContact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}