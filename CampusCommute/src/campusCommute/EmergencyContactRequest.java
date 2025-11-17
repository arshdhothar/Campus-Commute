package campusCommute;

public class EmergencyContactRequest {
    private String email;
    private String contactName;
    private String contactPhone;

    public EmergencyContactRequest(String email, String contactName, String contactPhone) {
        this.email = email;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
    }
}