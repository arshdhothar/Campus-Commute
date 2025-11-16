package com.campuscommute.server;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ID;

	private String email, phone, fName, lName, DOB, password, address, userType;
	private String emergencyContactName, emergencyContactPhone;

	@Column(length = 1000)
	private String bio;
	
	private String photoPath;
	
	// --- NEW FIELDS FOR US15 ---
	private boolean notifyOnConfirmation = true;
	private boolean notifyOnCancellation = true;
	private boolean notifyOnReminder = true;

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

	// --- Getters and setters ---

	public Long getID() {
		return ID;
	}

	public void setID(Long ID) {
		this.ID = ID;
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

	public String getEmergencyContactName() {
		return emergencyContactName;
	}

	public void setEmergencyContactName(String emergencyContactName) {
		this.emergencyContactName = emergencyContactName;
	}

	public String getEmergencyContactPhone() {
		return emergencyContactPhone;
	}

	public void setEmergencyContactPhone(String emergencyContactPhone) {
		this.emergencyContactPhone = emergencyContactPhone;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	// --- NEW GETTERS/SETTERS FOR US15 ---
	
	public boolean isNotifyOnConfirmation() {
		return notifyOnConfirmation;
	}

	public void setNotifyOnConfirmation(boolean notifyOnConfirmation) {
		this.notifyOnConfirmation = notifyOnConfirmation;
	}

	public boolean isNotifyOnCancellation() {
		return notifyOnCancellation;
	}

	public void setNotifyOnCancellation(boolean notifyOnCancellation) {
		this.notifyOnCancellation = notifyOnCancellation;
	}

	public boolean isNotifyOnReminder() {
		return notifyOnReminder;
	}

	public void setNotifyOnReminder(boolean notifyOnReminder) {
		this.notifyOnReminder = notifyOnReminder;
	}
}