package mcgill.ecse321.grocerystore;

public class User {
    public static String username;

    public User(String username) {
        this.username = username;
    }

    public static String getUsername() {
        return username;
    }

}
