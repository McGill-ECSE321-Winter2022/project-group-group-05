package mcgill.ecse321.grocerystore;

public class User {
    public static String username;
    public static String userType;
    private static User INSTANCE;

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

    public static String getUsername() {
        return username;
    }

    public static String getUserType() {
        return userType;
    }
}
