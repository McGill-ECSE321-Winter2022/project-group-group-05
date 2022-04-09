package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CustomerProfileActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        ((TextView) findViewById(R.id.username)).setText(User.getInstance().getUsername());
        getUser();
    }

    public void onRadioButtonClicked(View view) {
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
    }

    public void save(View v) {
        ProgressBar bar = findViewById(R.id.loading);
        RequestParams rp = new RequestParams();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        String address = ((TextView) findViewById(R.id.address)).getText().toString();

        //perform input validation and throw error
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
        } else if (email == null || email.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (address == null || address.trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your address", Toast.LENGTH_SHORT).show();
        } else {
            bar.setVisibility(View.VISIBLE);
            rp.add("password", ((TextView) findViewById(R.id.password)).getText().toString());
            rp.add("email", ((TextView) findViewById(R.id.email)).getText().toString());
            rp.add("address", ((TextView) findViewById(R.id.address)).getText().toString());
            if (((RadioButton) findViewById(R.id.radio_yes)).isChecked()) {
                rp.add("isLocal", "true");
            } else {
                rp.add("isLocal", "false");
            }

            HttpUtils.patch("customer/" + User.getInstance().getUsername(), rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                    bar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(getApplicationContext(), "Email is invalid", Toast.LENGTH_SHORT).show();
                    bar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void getUser() {
        HttpUtils.get("customer/" + User.getInstance().getUsername(), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject responseDto = new JSONObject(response.toString());
                    String password = responseDto.getString("password");
                    String email = responseDto.getString("email");
                    String address = responseDto.getString("address");
                    String local = responseDto.getString("local");

                    //prefill the text fields with the user's information
                    ((TextView) findViewById(R.id.password)).setText(password);
                    ((TextView) findViewById(R.id.email)).setText(email);
                    ((TextView) findViewById(R.id.address)).setText(address);

                    //set value for the radio button group
                    if (local.equals("true")) {
                        ((RadioButton) findViewById(R.id.radio_yes)).setChecked(true);
                    } else {
                        ((RadioButton) findViewById(R.id.radio_no)).setChecked(true);
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

}