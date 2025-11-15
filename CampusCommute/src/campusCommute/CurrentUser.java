package campusCommute;

public class CurrentUser {

    // The user currently logged in (null if none)
    public static Person loggedInUser = null;

    /**
     * Safely returns the logged-in user's ID.
     */
    public static Long getId() {
        if (loggedInUser == null) {
            return -1L;
        }
        return loggedInUser.getID();
    }

    /**
     * Returns full name (First + Last). Falls back to "Guest".
     */
    public static String getName() {
        if (loggedInUser == null) return "Guest";

        String f = loggedInUser.getfName();
        String l = loggedInUser.getlName();

        if (f == null && l == null) return "Guest";

        return ((f != null) ? f : "") + " " + ((l != null) ? l : "");
    }

    /**
     * Convenience check.
     */
    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
