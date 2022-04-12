package mcgill.ecse321.grocerystore;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * This page is used to draw the Customer's Cart
 */
public class CartActivity extends AppCompatActivity {

    private int cartId = -1;
    private boolean isLocal;
    private boolean isDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        // Fetch locality of the logged in customer, this will be used to calculate delivery fee.
        HttpUtils.get("/customer/" + User.getInstance().getUsername(), new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    isLocal = response.getBoolean("local");
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
        // Fetch the items from the Customer's cart and display them on screen
        // This page can only be reached if a valid customer is logged in. Therefore, this
        // request should not throw an error in normal operation.
        RequestParams getCartRequest = new RequestParams();
        getCartRequest.add("username", User.getInstance().getUsername());
        HttpUtils.post("/purchase/cart/", getCartRequest, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                TableLayout itemTable = findViewById(R.id.itemTable);
                try {
                    cartId = Integer.parseInt(response.getString("id"));
                    JSONArray specificItems = response.getJSONArray("specificItems");
                    for (int i = 0; i < specificItems.length(); i++) {
                        JSONObject specificItem = specificItems.getJSONObject(i);
                        itemTable.addView(createItemRow(specificItem));
                    }
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
        // Set up the Order Selection Spinner
        Spinner orderTypeSelector = findViewById(R.id.orderTypeSpinner);
        ArrayAdapter<CharSequence> orderChoices = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        orderChoices.addAll("Pick Up", "Delivery");
        orderTypeSelector.setAdapter(orderChoices);
        orderTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String orderType = (String) parent.getItemAtPosition(i);
                // change isDelivery boolean to correspond with what was selected, and update the price
                isDelivery = orderType.equals("Delivery");
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing; nothing was selected
            }
        });
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
    }

    public void order(View v) {
        // Set the order type of the purchase
        RequestParams setDelivery = new RequestParams();
        setDelivery.add("isDelivery", String.valueOf(isDelivery));
        HttpUtils.post("/purchase/setIsDelivery/" + cartId, setDelivery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Attempt to pay for the order if the delivery value was properly set
                HttpUtils.post("/purchase/pay/" + cartId, new RequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // purchase successfully completed, return to main page
                        Toast.makeText(getApplicationContext(), "Order Placed!", Toast.LENGTH_SHORT).show();
                        Intent mainPage = new Intent(CartActivity.this, CustomerMainActivity.class);
                        startActivity(mainPage);
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                try {
                    Toast.makeText(getApplicationContext(), statusCode + ": " + response.get("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Helper method used to update the price displayed on the app
     */
    private void updateTotalPrice() {
        // Fetch the items from the Customer's cart and calculate their total price
        RequestParams getCartRequest = new RequestParams();
        getCartRequest.add("username", User.getInstance().getUsername());
        HttpUtils.post("/purchase/cart/", getCartRequest, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray specificItems = response.getJSONArray("specificItems");
                    double totalPrice = 0;
                    for (int i = 0; i < specificItems.length(); i++) {
                        JSONObject specificItem = specificItems.getJSONObject(i);
                        totalPrice += Integer.parseInt(specificItem.getString("purchaseQuantity")) * Double.parseDouble(specificItem.getString("purchasePrice"));
                    }
                    // handle fee calculation and update delivery fee notification
                    if (!isLocal && isDelivery) {
                        totalPrice += 10;
                        findViewById(R.id.deliveryFeeLabel).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.deliveryFeeLabel).setVisibility(View.GONE);
                    }
                    // update total price label
                    String priceLabel = "Total: " + FormatUtils.formatCurrency(totalPrice);
                    ((TextView) findViewById(R.id.totalPriceLabel)).setText(priceLabel);
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

    /**
     * Generates a TableRow View object for the provided specificItem. This will eventually be used to populate the
     * itemTable.
     *
     * @param specificItem - the item in the purchase
     * @return a TableRow object formatted with the information in the parameter
     * @throws JSONException if the JSONObject provided is invalid
     */
    private TableRow createItemRow(JSONObject specificItem) throws JSONException {
        // Create TableRow object
        TableRow itemEntry = new TableRow(this);
        TableLayout.LayoutParams entryFormat = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        entryFormat.setMargins(0, 0, 0, 1);
        itemEntry.setLayoutParams(entryFormat);
        itemEntry.setPadding(15, 20, 0, 20);
        itemEntry.setBackgroundColor(getResources().getColor(R.color.white));

        // Create column for the item name
        final TextView item = new TextView(this);
        item.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(4, 4, 4, 4);
        item.setText(specificItem.getJSONObject("item").getString("name"));
        item.setBackgroundColor(getResources().getColor(R.color.white));
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Create column for the item purchase quantity
        final TextView purchaseQuantity = new TextView(this);
        purchaseQuantity.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        purchaseQuantity.setGravity(Gravity.CENTER);
        purchaseQuantity.setPadding(4, 4, 4, 4);
        purchaseQuantity.setText(specificItem.getString("purchaseQuantity"));
        purchaseQuantity.setBackgroundColor(getResources().getColor(R.color.white));
        purchaseQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Create column for the item purchase price
        final TextView purchasePrice = new TextView(this);
        purchasePrice.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        purchasePrice.setGravity(Gravity.CENTER);
        purchasePrice.setPadding(4, 4, 4, 4);
        // calculate price of the item and convert to a formatted string
        double priceOfItem = Double.parseDouble(specificItem.getString("purchasePrice"));
        int itemQuantity = Integer.parseInt(specificItem.getString("purchaseQuantity"));
        purchasePrice.setText(FormatUtils.formatCurrency(itemQuantity * priceOfItem));
        purchasePrice.setBackgroundColor(getResources().getColor(R.color.white));
        purchasePrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Create column for the item availability
        final TextView availability = new TextView(this);
        availability.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        availability.setGravity(Gravity.CENTER);
        availability.setPadding(4, 4, 4, 4);
        // Evaluate availability of the item and set the column contents accordingly
        String availabilityOfItem;
        boolean canDeliver = specificItem.getJSONObject("item").getBoolean("canDeliver");
        boolean canPickUp = specificItem.getJSONObject("item").getBoolean("canPickUp");
        if (canDeliver && canPickUp) {
            availabilityOfItem = "Both";
        } else if (canDeliver) {
            availabilityOfItem = "Delivery Only";
        } else if (canPickUp) {
            availabilityOfItem = "Pick Up Only";
        } else {
            availabilityOfItem = "Not available for Online Order";
        }
        availability.setText(availabilityOfItem);
        availability.setBackgroundColor(getResources().getColor(R.color.white));
        availability.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        // Add columns to the table row
        itemEntry.addView(item);
        itemEntry.addView(purchaseQuantity);
        itemEntry.addView(purchasePrice);
        itemEntry.addView(availability);

        itemEntry.setOnClickListener(view -> {
            final TableRow row = (TableRow) view;
            final String itemName = (String) ((TextView) row.getVirtualChildAt(0)).getText();
            AlertDialog.Builder removeItemAlert = new AlertDialog.Builder(CartActivity.this);
            removeItemAlert.setMessage("Remove " + itemName + " from Cart?");

            removeItemAlert.setPositiveButton("Ok", (dialog, btn) -> {
                // remove the item from the app ui
                TableLayout itemTable = findViewById(R.id.itemTable);
                itemTable.removeView(row);

                // remove the item from the customer's purchase in the backend
                RequestParams removeParams = new RequestParams();
                removeParams.add("itemName", itemName);
                removeParams.add("quantity", "0");
                HttpUtils.post("/purchase/setItem/" + cartId, removeParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // update price if the item was successfully removed
                        updateTotalPrice();
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
            });
            removeItemAlert.setNegativeButton("Cancel", (dialog, btn) -> {
                // if cancelled, do nothing and just close
            });
            removeItemAlert.show();
        });
        return itemEntry;
    }

}
