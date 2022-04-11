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
    private List<Integer> quantity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Intent intent = getIntent();
        loadItem(intent.getStringExtra("name"), this);
        Spinner quantitySpinner = (Spinner) findViewById(R.id.quantity_spinner);
        quantityAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, quantity);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(quantityAdapter);
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
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

    private void loadItem(String name, Context context) {
        HttpUtils.get("item/" + name, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                quantity.clear();
                try {
                    ((TextView) findViewById(R.id.item_name)).setText(response.getString("name"));
                    ((TextView) findViewById(R.id.item_price)).setText(displayPrice(response.getDouble("price")));
                    Glide.with(context).load(response.getString("image")).into((ImageView) findViewById(R.id.item_image));
                    ((TextView) findViewById(R.id.item_stock)).setText(response.getInt("inventory") + " in stock");
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

                    for (int i = 1; i <= response.getInt("inventory"); i++) {
                        quantity.add(i);
                    }
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