package campusCommute;

import java.io.*;

public class UserProfile implements Serializable {

    /** 
     * Serialization identifier to ensure compatibility during deserialization.
     */
    private static final long serialVersionUID = 1L;

    /** 
     * A short description or information about the user.
     */
    private String bio;

    /** 
     * The file path to the user's profile photo.
     */
    private String photoPath;

    /**
     * Constructs a new UserProfile with empty bio and photo path.
     */
    public UserProfile() {
        this.bio = "";
        this.photoPath = "";
    }

    /**
     * Retrieves the user's bio.
     * 
     * @return the bio of the user
     */
    public String getBio() {
        return bio;
    }

    /**
     * Updates the user's bio.
     * 
     * @param bio the new bio text to set
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Retrieves the file path of the user's profile photo.
     * 
     * @return the photo file path
     */
    public String getPhotoPath() {
        return photoPath;
    }

    /**
     * Updates the file path of the user's profile photo.
     * 
     * @param photoPath the new photo file path to set
     */
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    /**
     * Saves the current UserProfile object to a file using Java serialization.
     * The file will be stored in the "profiles" directory, which is created
     * automatically if it doesn't already exist.
     * 
     * @param filename the name (or path) of the file to save to
     * @throws IOException if an error occurs during file writing
     */
    public void saveProfile(String filename) throws IOException {
        File dir = new File("profiles");
        if (!dir.exists()) dir.mkdirs(); // Ensure folder exists
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(this);
        out.close();
    }

    /**
     * Loads a UserProfile object from a previosly saved file.
     * 
     * @param filename the name (or path) of the file to load from
     * @return the loaded UserProfile instance
     * @throws IOException if the file cannot be read
     * @throws ClassNotFoundException if the class definition cannot be found
     */
    public static UserProfile loadProfile(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
        UserProfile profile = (UserProfile) in.readObject();
        in.close();
        return profile;
    }
}

