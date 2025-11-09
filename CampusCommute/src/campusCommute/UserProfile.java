package campusCommute;

import java.io.*;

public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bio;
    private String photoPath;

    public UserProfile() {
        this.bio = "";
        this.photoPath = "";
    }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    // Save this profile to file
    public void saveProfile(String filename) throws IOException {
        File dir = new File("profiles");
        if (!dir.exists()) dir.mkdirs();  // ensure folder exists
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(this);
        out.close();
    }

    // Load profile from file
    public static UserProfile loadProfile(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        UserProfile profile = (UserProfile) in.readObject();
        in.close();
        return profile;
    }
}
