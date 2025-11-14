package com.campuscommute.server;

public class EmergencyContactRequest {
    private String email;
    private String contactName;
    private String contactPhone;

    // --- Getters are needed for Spring to read the JSON ---
    public String getEmail() {
        return email;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }
}