package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Perform input validation and throw error
            if (username == null || username.trim().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
            } else if (password == null || password.trim().length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
            } else {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginHttpRequest(username, password, loadingProgressBar);
            }
        });
    }

    private void updateUiWithUser(String username, String userType) {
        String welcome = getString(R.string.welcome) + username;
        User.getInstance().setUsername(username);
        User.getInstance().setUserType(userType);

        // Redirect current user to different pages depending on their user type
        if (userType.equals("Customer")) {
            Intent customerPage = new Intent(this, CustomerMainActivity.class);
            startActivity(customerPage);
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        } else if (userType.equals("Employee") || userType.equals("Owner")) {
            Intent staffPage = new Intent(this, StaffMainActivity.class);
            startActivity(staffPage);
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void loginHttpRequest(String username, String password, ProgressBar loadingProgressBar) {
        HttpUtils.get("employee/" + username, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String expectPassword = new JSONObject(response.toString()).getString("password");
                    if (password.equals(expectPassword)) {
                        updateUiWithUser(username, "Employee");
                    } else {
                        showLoginFailed(R.string.login_failed);
                    }
                    loadingProgressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                HttpUtils.get("customer/" + username, new RequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (username.equals("kiosk")) {
                                showLoginFailed(R.string.kiosk_login);
                            }
                            else {
                                String expectPassword = new JSONObject(response.toString()).getString("password");
                                if (password.equals(expectPassword)) {
                                    updateUiWithUser(username, "Customer");
                                } else {
                                    showLoginFailed(R.string.login_failed);
                                }
                            }
                            loadingProgressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        HttpUtils.get("owner/" + username, new RequestParams(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                try {
                                    String expectPassword = new JSONObject(response.toString()).getString("password");
                                    if (password.equals(expectPassword)) {
                                        updateUiWithUser(username, "Owner");
                                    } else {
                                        showLoginFailed(R.string.login_failed);
                                    }
                                    loadingProgressBar.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                showLoginFailed(R.string.login_failed);
                                loadingProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });

    }

    public void main(View v) {
        Intent mainPage = new Intent(this, MainActivity.class);
        startActivity(mainPage);
    }

    public void register(View v) {
        Intent registerPage = new Intent(this, CreateAccountActivity.class);
        startActivity(registerPage);
    }
}