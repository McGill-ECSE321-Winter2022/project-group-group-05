package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * This page displays the inventory and properties of all the items in the system to the staff
 * It also serves as the landing page for when the staff login to the app
 */
public class StaffMainActivity extends AppCompatActivity {

    // Used to display the Sidebar
    private ActionBarDrawerToggle actionBarDrawerToggle;
    // Holds the UI Cards for each item in the database
    private HashMap<String, LinearLayout> itemLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);
        DrawerLayout drawerLayout = findViewById(R.id.staff_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        itemLayouts = new HashMap<>();
        MenuItem schedule = ((NavigationView) findViewById(R.id.staff_navigation)).getMenu().findItem(R.id.staff_schedule);
        if (User.getInstance().getUserType().equals("Owner")) {
            schedule.setVisible(false);
        }
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fetch all the items in the system and populate itemLayouts
        HttpUtils.get("/item/getAll", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = response.getJSONObject(i);
                        itemLayouts.put(item.getString("name"), createItemEntry(item));
                    }
                    EditText searchBar = findViewById(R.id.searchBar);
                    searchBar.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            // do nothing, behavior is already handled by onTextChanged
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            updateItemDisplay(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            // do nothing, behavior is already handled by onTextChanged
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), statusCode + ": " + response.get("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(MenuItem item) {
        Intent mainPage = new Intent(this, MainActivity.class);
        startActivity(mainPage);
    }

    public void profile(MenuItem item) {
        Intent profilePage = new Intent(this, StaffProfileActivity.class);
        startActivity(profilePage);
    }

    public void paidOrder(MenuItem item) {
        Intent paidOrderPage = new Intent(this, StaffPaidOrderActivity.class);
        startActivity(paidOrderPage);
    }

    public void completedOrder(MenuItem item) {
        Intent completedOrderPage = new Intent(this, StaffCompletedOrderActivity.class);
        startActivity(completedOrderPage);
    }

    public void schedule(MenuItem item) {
        Intent schedulePage = new Intent(this, StaffScheduleActivity.class);
        startActivity(schedulePage);
    }

    /**
     * Helper Method used to update the UI item list with only the items that match the search query
     *
     * @param searchQuery - String representing what the User searched for
     */
    private void updateItemDisplay(CharSequence searchQuery) {
        LinearLayout itemView = findViewById(R.id.itemList);
        itemView.removeAllViews();
        for (String itemName : itemLayouts.keySet()) {
            if (itemName.contains(searchQuery)) {
                itemView.addView(itemLayouts.get(itemName));
            }
        }
    }

    private LinearLayout createItemEntry(JSONObject item) throws JSONException {
        LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Create layout to hold item information
        LinearLayout itemCard = new LinearLayout(this);
        itemCard.setLayoutParams(itemLayoutParams);
        itemCard.setPadding(30, 30, 30, 30);
        itemCard.setBackgroundColor(getResources().getColor(R.color.grey));
        itemCard.setOrientation(LinearLayout.VERTICAL);

        // Create label for the name of the item
        TextView itemName = new TextView(this);
        itemName.setLayoutParams(itemLayoutParams);
        itemName.setGravity(Gravity.CENTER);
        itemName.setPadding(20, 20, 20, 20);
        itemName.setBackgroundColor(getResources().getColor(R.color.grocery));
        itemName.setTextColor(getResources().getColor(R.color.white));
        itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        itemName.setText(item.getString("name"));

        // Create label for the price
        TextView itemPrice = new TextView(this);
        itemPrice.setLayoutParams(itemLayoutParams);
        itemPrice.setGravity(Gravity.CENTER_VERTICAL);
        itemPrice.setPadding(20, 20, 20, 0);
        itemPrice.setBackgroundColor(getResources().getColor(R.color.white));
        itemPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        String priceText = "Price: " + formatTime(scheduledShifts.getJSONObject(i).getJSONObject("shift").getString("startTime"));
        itemPrice.setText(startTimeText);


    }
}