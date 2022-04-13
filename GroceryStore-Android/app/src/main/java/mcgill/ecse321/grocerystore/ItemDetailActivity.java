package mcgill.ecse321.grocerystore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ItemDetailActivity extends AppCompatActivity {
    private ArrayAdapter<Integer> quantityAdapter;
    private final List<Integer> quantity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Set up the spinner for choosing item quantity
        Spinner quantitySpinner = findViewById(R.id.quantity_spinner);
        quantityAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, quantity);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);

        // Display the information of this item
        loadItem(getIntent().getStringExtra("name"), this, quantityAdapter);
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
    }

    private void loadItem(String name, Context context, final ArrayAdapter<Integer> adapter) {
        HttpUtils.get("item/" + name, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                quantity.clear();
                try {
                    ((TextView) findViewById(R.id.item_name)).setText(response.getString("name"));
                    ((TextView) findViewById(R.id.item_price)).setText(FormatUtils.formatCurrency(response.getDouble("price")));
                    if (response.getString("image").trim().length() > 0) {
                        Glide.with(context).load(response.getString("image")).into((ImageView) findViewById(R.id.item_image));
                    }
                    ((TextView) findViewById(R.id.item_stock)).setText(response.getInt("inventory") + " in stock");

                    // Show if the item is available for delivery or pick-up
                    if (response.getBoolean("canDeliver")) {
                        ((TextView) findViewById(R.id.item_delivery)).setText("available for delivery");
                    } else {
                        ((TextView) findViewById(R.id.item_delivery)).setText("not available for delivery");
                    }
                    if (response.getBoolean("canPickUp")) {
                        ((TextView) findViewById(R.id.item_pickup)).setText("available for pick up");
                    } else {
                        ((TextView) findViewById(R.id.item_pickup)).setText("not available for delivery");
                    }

                    // Update the spinner
                    if (response.getInt("inventory") > 0) {
                        for (int i = 1; i <= response.getInt("inventory"); i++) {
                            quantity.add(i);
                        }
                    } else {
                        // If current stock is empty, disable the button
                        findViewById(R.id.cart_button).setEnabled(false);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addToCart(View v) {

        final Spinner quantitySpinner = findViewById(R.id.quantity_spinner);

        // Get the cart of current user
        RequestParams rpCart = new RequestParams();
        rpCart.add("username", User.getInstance().getUsername());

        HttpUtils.post("purchase/cart", rpCart, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Add this item to the cart
                    String name = getIntent().getStringExtra("name");
                    Long id = response.getLong("id");
                    RequestParams rpItem = new RequestParams();
                    rpItem.add("itemName", name);
                    rpItem.add("quantity", quantitySpinner.getSelectedItem().toString());
                    HttpUtils.post("purchase/addItem/" + id, rpItem, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Successfully added " + name + " to the cart", Toast.LENGTH_LONG).show();
                            main(v);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(getApplicationContext(), "Quantity of " + name + " exceeds current inventory", Toast.LENGTH_LONG).show();

                        }
                    });
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Loading failed", Toast.LENGTH_LONG).show();

            }
        });

    }


}