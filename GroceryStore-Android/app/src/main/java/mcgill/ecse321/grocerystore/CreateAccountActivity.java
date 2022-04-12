package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CreateAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, MainActivity.class);
        startActivity(mainPage);
    }

    /**
     * Called when "create account" button is clicked. Simply call the backend API and display error if needed.
     *
     * @param v the view
     */
    public void create(View v) {
        ProgressBar spinner = findViewById(R.id.loading);
        RequestParams rp = new RequestParams();
        String username = ((TextView) findViewById(R.id.username)).getText().toString();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        String address = ((TextView) findViewById(R.id.address)).getText().toString();
        Boolean isLocal = ((CheckBox) findViewById(R.id.isLocal)).isChecked();
        /*
        Verify that all fields are filled.
        Note: these fields will never be null because of the way they're obtained, so no need for null check
         */
        if (username.trim().length() == 0 || password.trim().length() == 0 ||
                email.trim().length() == 0 || address.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill all fields!", Toast.LENGTH_SHORT).show();
        } else {
            spinner.setVisibility(View.VISIBLE);
            rp.add("password", password);
            rp.add("email", email);
            rp.add("address", address);
            rp.add("isLocal", String.valueOf(isLocal)); // http requests are strings

            HttpUtils.post("customer/" + username, rp, new JsonHttpResponseHandler() {
                // On success, redirect the user to CustomerMainActivity
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    redirectUser(username);
                }

                // On failure, show the error message thrown by the backend
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), errorResponse.optString("message", "Invalid inputs!"), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Sign in the newly created user and redirect to CustomerMainActivity
     *
     * @param username username of the new user
     */
    private void redirectUser(String username) {
        User.getInstance().setUsername(username);
        User.getInstance().setUserType("Customer");
        Intent customerPage = new Intent(this, CustomerMainActivity.class);
        startActivity(customerPage);
    }
}