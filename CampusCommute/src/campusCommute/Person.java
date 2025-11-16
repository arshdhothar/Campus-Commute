package campusCommute; 

import com.google.gson.annotations.SerializedName;

/**
 * Frontend data model for a Person.
 * This class must mirror the fields in the backend Person entity
 * to ensure Gson can serialize and deserialize objects correctly.
 */
public class Person {
    
    // This tells Gson to use "id" (lowercase) in the JSON
    // for both reading and writing, fixing the sign-up bug.
    @SerializedName("id")
	private Long ID; 
    
	private String email, phone, fName, lName, DOB, password, address, userType;
	private String emergencyContactName, emergencyContactPhone;
	private String bio;
	private String photoPath;

    // --- NEW FIELDS FOR US15 ---
    private boolean notifyOnConfirmation;
    private boolean notifyOnCancellation;
    private boolean notifyOnReminder;

	// -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------

	public Person() {
		
	}

	public Person(String email, String phone, String fName, String lName, String DOB, String password, String address, String userType) {
		this.email = email;
		this.phone = phone;
		this.fName = fName;
		this.lName = lName;
		this.DOB = DOB;
		this.password = password;
		this.address = address;
		this.userType = userType;
	}

	// -----------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------

	public Long getID() {
		return ID;
	}

	public String getEmail() {
		return email;
	}

	public String getfName() {
		return fName;
	}

	public String getlName() {
		return lName;
	}

	public String getDOB() {
		return DOB;
	}

	public String getPassword() {
		return password;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public String getUserType() {
		return userType;
	}

	public String getEmergencyContactName() {
		return emergencyContactName;
	}

	public String getEmergencyContactPhone() {
		return emergencyContactPhone;
	}

	public String getBio() {
		return bio;
	}

	public String getPhotoPath() {
		return photoPath;
	}

    // --- NEW GETTERS FOR US15 ---
    public boolean isNotifyOnConfirmation() {
        return notifyOnConfirmation;
    }
    public boolean isNotifyOnCancellation() {
        return notifyOnCancellation;
    }
    public boolean isNotifyOnReminder() {
        return notifyOnReminder;
    }

    // -----------------------------------------------------------------
    // SETTERS
    // -----------------------------------------------------------------
	
	public void setID(Long ID) {
		this.ID = ID;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setfName(String fName) { this.fName = fName; }

	public void setlName(String lName) { this.lName = lName; }

	public void setDOB(String dOB) { this.DOB = dOB; }

	public void setPassword(String password) { this.password = password; }

	public void setPhone(String phone) { this.phone = phone; }

	public void setAddress(String address) { this.address = address; }

	public void setUserType(String userType) { this.userType = userType; }

	public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

	public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

	public void setBio(String bio) { this.bio = bio; }

	public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    // --- NEW SETTERS FOR US15 ---
    public void setNotifyOnConfirmation(boolean notifyOnConfirmation) { this.notifyOnConfirmation = notifyOnConfirmation; }

    public void setNotifyOnCancellation(boolean notifyOnCancellation) { this.notifyOnCancellation = notifyOnCancellation; }

    public void setNotifyOnReminder(boolean notifyOnReminder) { this.notifyOnReminder = notifyOnReminder; }
}