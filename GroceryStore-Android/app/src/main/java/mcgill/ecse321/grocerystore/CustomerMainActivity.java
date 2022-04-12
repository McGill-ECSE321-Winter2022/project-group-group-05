package mcgill.ecse321.grocerystore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CustomerMainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayAdapter<Item> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);
        drawerLayout = findViewById(R.id.customer_drawer_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView itemListview = (ListView) findViewById(R.id.item_list);
        adapter = new ItemAdapter(this, items);
        itemListview.setAdapter(adapter);
        itemListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (items.get(position).available) {
                    itemDetail(view, items.get(position).name);
                }
            }

        });
        refreshLists(this.getCurrentFocus());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart(view);
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

    public void refreshLists(View view) {
        loadItems(adapter, items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                filterItems(adapter, items, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Inner class that stores partial information of an item
     */
    class Item {
        public String name;
        public String image;
        public double price;
        public boolean available;

        public Item(String name, String image, double price, boolean available) {
            this.name = name;
            this.image = image;
            this.price = price;
            this.available = available;
        }
    }

    class ItemAdapter extends ArrayAdapter<Item> {
        public ItemAdapter(Context context, ArrayList<Item> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Item item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_main, parent, false);
            }

            // Display the item information
            ((TextView) convertView.findViewById(R.id.item_name)).setText(item.name);
            ((TextView) convertView.findViewById(R.id.item_price)).setText(FormatUtils.formatCurrency(item.price));
            if (item.image.trim().length() > 0) {
                Glide.with(getContext()).load(item.image).into((ImageView) convertView.findViewById(R.id.item_image));
            } else {
                ((ImageView) convertView.findViewById(R.id.item_image)).setImageResource(R.drawable.ic_baseline_image_not_supported_24);
            }
            if (item.available) {
                ((TextView) convertView.findViewById(R.id.item_available)).setVisibility(View.INVISIBLE);
            } else {
                ((TextView) convertView.findViewById(R.id.item_available)).setVisibility(View.VISIBLE);
            }

            //Return the completed view to render on screen
            return convertView;
        }
    }

    private void loadItems(final ArrayAdapter<Item> adapter, final List<Item> items) {
        HttpUtils.get("item/getAll", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                items.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject itemJSON = response.getJSONObject(i);
                        Boolean availableOnline = itemJSON.getBoolean("canDeliver") || itemJSON.getBoolean("canPickUp");
                        items.add(new Item(itemJSON.getString("name"), itemJSON.getString("image"), itemJSON.getDouble("price"), availableOnline));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterItems(final ArrayAdapter<Item> adapter, final List<Item> items, String filterText) {
        HttpUtils.get("item/getAll", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                items.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject itemJSON = response.getJSONObject(i);

                        // Add the item to the list only if its name contains the required string
                        if (itemJSON.getString("name").toLowerCase().contains(filterText.toLowerCase())) {
                            Boolean availableOnline = itemJSON.getBoolean("canDeliver") || itemJSON.getBoolean("canPickUp");
                            items.add(new Item(itemJSON.getString("name"), itemJSON.getString("image"), itemJSON.getDouble("price"), availableOnline));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logout(MenuItem item) {
        Intent mainPage = new Intent(this, MainActivity.class);
        startActivity(mainPage);
    }

    public void itemDetail(View v, String name) {
        Intent itemPage = new Intent(this, ItemDetailActivity.class);
        itemPage.putExtra("name", name);
        startActivity(itemPage);
    }

    public void history(MenuItem item) {
        Intent historyPage = new Intent(this, PurchaseHistoryActivity.class);
        startActivity(historyPage);
    }

    public void profile(MenuItem item) {
        Intent profilePage = new Intent(this, CustomerProfileActivity.class);
        startActivity(profilePage);
    }

    public void cart(View v) {
        Intent cartPage = new Intent(this, CartActivity.class);
        startActivity(cartPage);
    }
}