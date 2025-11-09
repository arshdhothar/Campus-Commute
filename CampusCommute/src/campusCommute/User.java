package campusCommute;

/**
 * Represents a user in the CampusCommute system.
 * Each user has a unique username that identifies them.
 */
public class User {

    /** 
     * The username associated with this user.
     */
    private String username;

    /**
     * Constructs a new User object with the specified username.
     *
     * @param username the username of the user
     */
    public User(String username) {
        this.username = username;
    }

    /**
     * Retrieves the username of this user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }
}