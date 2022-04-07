package mcgill.ecse321.grocerystore.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userType;
    private String username;

    public LoggedInUser() {
        this.userType = null;
        this.username = null;
    }

    public LoggedInUser(String userType, String username) {
        this.userType = userType;
        this.username = username;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserType() {
        return userType;
    }

    public String getUsername() {
        return username;
    }
}