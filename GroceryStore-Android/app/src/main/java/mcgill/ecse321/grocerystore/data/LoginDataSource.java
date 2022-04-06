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
    String error;

    public Result<LoggedInUser> login(String username, String password) {
        HttpUtils.get("owner/" + username, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    String expectPassword = new JSONObject(response.toString()).getString("password");
                    if (password.equals(expectPassword)) {
                        LoggedInUser owner =
                                new LoggedInUser(
                                        "Owner",
                                        username);
                        user = new Result.Success<>(owner);
                    } else {
                        error = "Wrong password";
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                HttpUtils.get("employee/" + username, new RequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                        try {
                            String expectPassword = new JSONObject(response.toString()).getString("password");
                            if (password.equals(expectPassword)) {
                                LoggedInUser owner =
                                        new LoggedInUser(
                                                "Employee",
                                                username);
                                user = new Result.Success<>(owner);
                            } else {
                                error = "Wrong password";
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        HttpUtils.get("customer/" + username, new RequestParams(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                try {
                                    String expectPassword = new JSONObject(response.toString()).getString("password");
                                    if (password.equals(expectPassword)) {
                                        LoggedInUser owner =
                                                new LoggedInUser(
                                                        "Customer",
                                                        username);
                                        user = new Result.Success<>(owner);
                                    } else {
                                        error = "Wrong password";
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                error = "User does not exist";

                            }
                        });
                    }
                });
            }
        });
        if(error == null){
            return user;
        }else {
            return new Result.Error(new IOException(error, new Exception()));
        }
    }

        public void logout () {
            // TODO: revoke authentication
        }
    }