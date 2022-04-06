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
    Result<LoggedInUser> user;

    public Result<LoggedInUser> login(String username, String password) {
        String error = null;
        HttpUtils.get("owner/" + username, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                if () {
                } else {
                    LoggedInUser owner =
                            new LoggedInUser(
                                    "Owner",
                                    username);
                    user = new Result.Success<>(owner);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                HttpUtils.get("employee/" + username, new RequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        LoggedInUser owner =
                                new LoggedInUser(
                                        "Owner",
                                        username);
                        user = new Result.Success<>(owner);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {


                    }

                });
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}