package mcgill.ecse321.grocerystore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * This page displays the inventory and properties of all the items in the system to the staff
 * It also serves as the landing page for when the staff login to the app
 */
public class StaffMainActivity extends AppCompatActivity {

    // Used to display the Sidebar
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private final ArrayList<Item> items = new ArrayList<>();
    private ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);
        DrawerLayout drawerLayout = findViewById(R.id.staff_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
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

        // set up item list
        final ListView itemListView = findViewById(R.id.item_list);
        adapter = new ItemAdapter(this, items);
        itemListView.setAdapter(adapter);
        updateItemDisplay("", adapter, items);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search bar
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateItemDisplay(newText, adapter, items);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Inner class that stores partial information of an item
     */
    private class Item {
        public String name;
        public double price;
        public int inventory;
        public boolean canDeliver;
        public boolean canPickUp;
        public boolean discontinued;

        public Item(String name, double price, int inventory, boolean canDeliver, boolean canPickUp, boolean discontinued) {
            this.name = name;
            this.price = price;
            this.inventory = inventory;
            this.canDeliver = canDeliver;
            this.canPickUp = canPickUp;
            this.discontinued = discontinued;
        }
    }

    private class ItemAdapter extends ArrayAdapter<Item> {
        public ItemAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_staff, parent, false);
            }
            // Display the item information
            ((TextView) convertView.findViewById(R.id.itemName)).setText(item.name);
            ((TextView) convertView.findViewById(R.id.itemPrice)).setText(String.format("Price: %s", FormatUtils.formatCurrency(item.price)));
            ((TextView) convertView.findViewById(R.id.itemInventory)).setText(String.format("Inventory: %s", item.inventory));
            ((TextView) convertView.findViewById(R.id.itemCanDeliver)).setText(String.format("Can Deliver: %s", item.canDeliver));
            ((TextView) convertView.findViewById(R.id.itemCanPickUp)).setText(String.format("Can Pick Up: %s", item.canPickUp));
            ((TextView) convertView.findViewById(R.id.itemDiscontinued)).setText(String.format("Discontinued: %s", item.discontinued));
            //Return the completed view to render on screen
            return convertView;
        }
    }

    private void updateItemDisplay(String searchQuery, final ArrayAdapter<Item> adapter, final List<Item> items) {
        HttpUtils.get("item/getAll", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                items.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        if (item.getString("name").toLowerCase().contains(searchQuery.toLowerCase())) {
                            String itemName = item.getString("name");
                            double itemPrice = item.getDouble("price");
                            int itemInventory = item.getInt("inventory");
                            boolean canDeliver = item.getBoolean("canDeliver");
                            boolean canPickUp = item.getBoolean("canPickUp");
                            boolean discontinued = item.getBoolean("discontinued");
                            items.add(new Item(itemName, itemPrice, itemInventory, canDeliver, canPickUp, discontinued));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
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

}