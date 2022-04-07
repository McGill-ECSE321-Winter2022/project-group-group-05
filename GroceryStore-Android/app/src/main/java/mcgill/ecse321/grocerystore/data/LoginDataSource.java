package mcgill.ecse321.grocerystore.data;

import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import mcgill.ecse321.grocerystore.HttpUtils;
import mcgill.ecse321.grocerystore.data.model.LoggedInUser;

import java.io.IOException;

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