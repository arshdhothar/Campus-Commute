package campusCommute;

public class Person {

    private Long id;
    private String email;
    private String phone;
    private String fName;
    private String lName;
    private String dob;
    private String password;
    private String address;
    private String userType;

    // Fields needed for profile customization
    private String bio;
    private String photoPath;
    // private String emergencyContactName; // You can add these later
    // private String emergencyContactPhone;

    // Default constructor (required for Gson/JPA)
    public Person() {}

    // Constructor for creating a new user (optional but helpful)
    public Person(String email, String phone, String fName, String lName,
                  String dob, String password, String address, String userType) {

        this.email = email;
        this.phone = phone;
        this.fName = fName;
        this.lName = lName;
        this.dob = dob;
        this.password = password;
        this.address = address;
        this.userType = userType;
    }

    // --- GETTERS ---
    // These are needed to READ data from the object

    public Long getID() { return id; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getfName() { return fName; }
    public String getlName() { return lName; }
    public String getDob() { return dob; }
    public String getPassword() { return password; }
    public String getAddress() { return address; }
    public String getUserType() { return userType; }
    public String getBio() { return bio; }
    public String getPhotoPath() { return photoPath; }

    // --- SETTERS ---
    // These are needed for Gson to CREATE the object from JSON
    // and for your GUI to UPDATE the object's values

    public void setID(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setfName(String fName) { this.fName = fName; }
    public void setlName(String lName) { this.lName = lName; }
    public void setDob(String dob) { this.dob = dob; }
    public void setPassword(String password) { this.password = password; }
    public void setAddress(String address) { this.address = address; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setBio(String bio) { this.bio = bio; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}