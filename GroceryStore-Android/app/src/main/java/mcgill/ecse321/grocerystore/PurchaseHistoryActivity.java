package mcgill.ecse321.grocerystore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PurchaseHistoryActivity extends AppCompatActivity {
    private ArrayList<Purchase> purchases = new ArrayList<>();
    private ArrayAdapter<Purchase> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);
        final ListView purchaseListview = (ListView) findViewById(R.id.purchase_list);
        adapter = new PurchaseAdapter(this, purchases);
        purchaseListview.setAdapter(adapter);
        refreshLists(this.getCurrentFocus());
    }

    public void refreshLists(View view) {
        loadPurchases(adapter, purchases);
    }

    /**
     * Format item price to 2 decimal places
     *
     * @param price
     * @return string of price
     */
    public String displayPrice(double price) {
        return "$ " + String.format("%.2f", price);
    }

    /**
     * Inner class that stores information of a purchased item which will be displayed
     */
    class PurchaseItem {
        public String name;
        public double price;
        public int quantity;

        public PurchaseItem(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

    }

    /**
     * Inner class that stores information of a purchase which will be displayed
     */
    class Purchase {
        public double total;
        public boolean delivery;
        public long id;
        public String date;
        public String state;
        public ArrayList<PurchaseItem> items;

        public Purchase(boolean delivery, long id, String date, String state, ArrayList<PurchaseItem> items) {
            this.total = 0;
            this.delivery = delivery;
            this.id = id;
            this.date = date;
            this.items = items;
            this.state = state;
            for (PurchaseItem item : items) {
                total += item.price * item.quantity;
            }
        }
    }

    class PurchaseAdapter extends ArrayAdapter<Purchase> {
        public PurchaseAdapter(Context context, ArrayList<Purchase> purchases) {
            super(context, 0, purchases);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Purchase purchase = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.purchase, parent, false);
            }

            // Display purchased items
            LinearLayout layout = (LinearLayout) (convertView.findViewById(R.id.purchase_item_list));
            if (layout.getChildCount() == 0) {
                for (PurchaseItem item : purchase.items) {
                    View itemView = getLayoutInflater().inflate(R.layout.purchase_item, null);
                    ((TextView) itemView.findViewById(R.id.item_name)).setText(item.name);
                    ((TextView) itemView.findViewById(R.id.item_price)).setText(displayPrice(item.price));
                    ((TextView) itemView.findViewById(R.id.item_quantity)).setText("" + item.quantity);
                    layout.addView(itemView);
                }
            }

            // Display the purchase information
            ((TextView) convertView.findViewById(R.id.purchase_id)).setText("Order #" + purchase.id);
            ((TextView) convertView.findViewById(R.id.purchase_date)).setText("" + purchase.date);
            if (purchase.delivery) {
                ((TextView) convertView.findViewById(R.id.purchase_type)).setText("Order Type: delivery");
            } else {
                ((TextView) convertView.findViewById(R.id.purchase_type)).setText("Order Type: pick up");
            }
            ((TextView) convertView.findViewById(R.id.purchase_total)).setText("Total: " + displayPrice(purchase.total));
            ((TextView) convertView.findViewById(R.id.purchase_state)).setText("Order State: " + purchase.state);

            //Return the completed view to render on screen
            return convertView;
        }
    }

    private void loadPurchases(final ArrayAdapter<Purchase> adapter, final List<Purchase> purchases) {
        HttpUtils.get("customer/" + User.getInstance().getUsername() + "/getPurchases", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                purchases.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        ArrayList<PurchaseItem> items = new ArrayList<>();
                        JSONObject purchaseJSON = response.getJSONObject(i);
                        JSONArray purchaseItems = purchaseJSON.getJSONArray("specificItems");
                        for (int j = 0; j < purchaseItems.length(); j++) {
                            JSONObject purchaseItem = purchaseItems.getJSONObject(j);
                            JSONObject item = purchaseItem.getJSONObject("item");
                            PurchaseItem newItem = new PurchaseItem(item.getString("name"), purchaseItem.getDouble("purchasePrice"), purchaseItem.getInt("purchaseQuantity"));
                            items.add(newItem);
                        }
                        purchases.add(new Purchase(purchaseJSON.getBoolean("delivery"), purchaseJSON.getLong("id"), purchaseJSON.getString("dateOfPurchase"), purchaseJSON.getString("state"), items));
                    } catch (Exception e) {

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

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
    }
}