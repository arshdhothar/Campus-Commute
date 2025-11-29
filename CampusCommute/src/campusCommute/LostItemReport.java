package campusCommute;

public class LostItemReport {
    private String userEmail;
    private String itemDescription;
    private String preferredContact;
    private String driverInfo;

    // Constructor
    public LostItemReport(String userEmail, String itemDescription, String preferredContact, String driverInfo) {
        this.userEmail = userEmail;
        this.itemDescription = itemDescription;
        this.preferredContact = preferredContact;
        this.driverInfo = driverInfo;
    }

    // Getters and Setters
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

	public String getDriverInfo() {
		return driverInfo;
	}

	public void setDriverInfo(String driverInfo) {
		this.driverInfo = driverInfo;
	}

    
}