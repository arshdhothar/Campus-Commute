package com.campuscommute.server;

// Import these new files
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // <-- ADD THIS: Tells Spring this class is a database table
public class Person {

	@Id // <-- ADD THIS: Marks this field as the primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // <-- ADD THIS: Makes the DB auto-generate the ID
	private Long ID; // <-- ADD THIS: Databases need a numeric primary key

	private String email, phone, fName, lName, DOB, password, address, userType;
	private String emergencyContactName, emergencyContactPhone;

	// A default constructor is required by JPA
	public Person() {
	}

	public Person(String email, String phone, String fName, String lName, String DOB, String password, String address,
			String userType) {
		this.email = email;
		this.phone = phone;
		this.fName = fName;
		this.lName = lName;
		this.DOB = DOB;
		this.password = password;
		this.address = address;
		this.userType = userType;
	}

	// getter and setters for class

	public Long getID() {
		return ID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getDOB() {
		return DOB;
	}

	public void setDOB(String dOB) {
		DOB = dOB;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the emergencyContactName
	 */
	public String getEmergencyContactName() {
		return emergencyContactName;
	}

	/**
	 * @param emergencyContactName the emergencyContactName to set
	 */
	public void setEmergencyContactName(String emergencyContactName) {
		this.emergencyContactName = emergencyContactName;
	}

	/**
	 * @return the emergencyContactPhone
	 */
	public String getEmergencyContactPhone() {
		return emergencyContactPhone;
	}

	/**
	 * @param emergencyContactPhone the emergencyContactPhone to set
	 */
	public void setEmergencyContactPhone(String emergencyContactPhone) {
		this.emergencyContactPhone = emergencyContactPhone;
	}
}