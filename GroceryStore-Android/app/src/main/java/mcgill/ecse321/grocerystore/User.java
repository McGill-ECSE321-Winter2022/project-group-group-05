package mcgill.ecse321.grocerystore;

/**
 * Singleton Class used to store the login data of the User
 */
public class User {

    private String username;
    private String userType;

    private static volatile User INSTANCE;

    public static User getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new User();
        }
        return INSTANCE;
    }

    private User() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }

}
