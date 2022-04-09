package mcgill.ecse321.grocerystore.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import mcgill.ecse321.grocerystore.CreateAccountActivity;
import mcgill.ecse321.grocerystore.CustomerMainActivity;
import mcgill.ecse321.grocerystore.HttpUtils;
import mcgill.ecse321.grocerystore.MainActivity;
import mcgill.ecse321.grocerystore.R;
import mcgill.ecse321.grocerystore.StaffMainActivity;
import mcgill.ecse321.grocerystore.User;
import mcgill.ecse321.grocerystore.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        loginButton.setEnabled(true);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (username == null || username.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_SHORT).show();
                } else if (password == null || password.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                } else {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginHttpRequest(username, password, v, loadingProgressBar);
                }
            }
        });
    }

    private void updateUiWithUser(String username, String userType) {
        String welcome = getString(R.string.welcome) + username;
        User.getInstance().setUsername(username);
        User.getInstance().setUserType(userType);
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

    public void loginHttpRequest(String username, String password, View v, ProgressBar loadingProgressBar) {
        HttpUtils.get("employee/" + username, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    String expectPassword = new JSONObject(response.toString()).getString("password");
                    if (password.equals(expectPassword)) {
                        updateUiWithUser(username, "Employee");
                    } else {
                        showLoginFailed(R.string.login_failed);
                    }
                    loadingProgressBar.setVisibility(View.GONE);
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
                                updateUiWithUser(username, "Customer");
                            } else {
                                showLoginFailed(R.string.login_failed);
                            }
                            loadingProgressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
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