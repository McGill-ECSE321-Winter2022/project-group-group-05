package mcgill.ecse321.grocerystore.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userType;
    private String userName;

    public LoggedInUser(String userId, String displayName) {
        this.userType = userId;
        this.userName = displayName;
    }

    public String getUserType() {
        return userType;
    }

    public String getUsername() {
        return userName;
    }
}