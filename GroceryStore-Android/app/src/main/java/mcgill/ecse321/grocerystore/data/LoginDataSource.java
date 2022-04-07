package mcgill.ecse321.grocerystore.data;

import java.io.IOException;

import mcgill.ecse321.grocerystore.data.model.LoggedInUser;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private String error;
    private LoggedInUser user = new LoggedInUser();

    public Result<LoggedInUser> login(String username, String userType) {
        if (username != null) {
            user.setUsername(username);
            user.setUserType(userType);
            return new Result.Success(user);
        } else {
            return new Result.Error(new IOException(error, new Exception()));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}