package mcgill.ecse321.grocerystore.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String userName;
    private String userType;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String userName, String userType) {
        this.userName = userName;
        this.userType = userType;
    }

    String getUsername() {
        return userName;
    }

    String getUserType() {
        return userType;
    }
}