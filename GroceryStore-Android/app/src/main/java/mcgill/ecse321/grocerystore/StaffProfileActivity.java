package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class StaffProfileActivity extends AppCompatActivity {
    //use lowercased input in http request
    String staff = User.getInstance().getUserType().toLowerCase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);
        ((TextView) findViewById(R.id.username)).setText(User.getInstance().getUsername());
        getUser();
    }


    public void main(View v) {
        Intent mainPage = new Intent(this, StaffMainActivity.class);
        startActivity(mainPage);
    }

    public void save(View v) {
        ProgressBar bar = findViewById(R.id.loading);
        RequestParams rp = new RequestParams();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();

        // Perform input validation and throw error
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
        } else {
            bar.setVisibility(View.VISIBLE);
            rp.add("password", ((TextView) findViewById(R.id.password)).getText().toString());
            rp.add("email", ((TextView) findViewById(R.id.email)).getText().toString());
            HttpUtils.patch(staff.concat("/") + User.getInstance().getUsername(), rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(), errorResponse.optString("message", "Invalid inputs!"), Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * get the user with given username using the http get request
     */
    private void getUser() {
        HttpUtils.get(staff.concat("/") + User.getInstance().getUsername(), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject responseDto = new JSONObject(response.toString());
                    String password = responseDto.getString("password");
                    String email = responseDto.getString("email");
                    // Prefill the text fields with the user's information
                    ((TextView) findViewById(R.id.password)).setText(password);
                    ((TextView) findViewById(R.id.email)).setText(email);
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.optString("message", "Invalid username!"), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

