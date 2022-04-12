package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fetch opening hours and display them
        setOpeningHours();

        // Fetch holidays and display them
        HttpUtils.get("holiday/getAll", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                TableLayout holidayTable = findViewById(R.id.holidayTable);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject holiday = response.getJSONObject(i);
                        holidayTable.addView(createHolidayRow(holiday));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), errorResponse.optString("message", "Database Error!"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(MenuItem item) {
        Intent loginPage = new Intent(this, LoginActivity.class);
        startActivity(loginPage);
        ;
    }

    /**
     * Set opening hours for every day of the week
     */
    private void setOpeningHours() {
        setOpeningHours("Monday", R.id.mondayHours);
        setOpeningHours("Tuesday", R.id.tuesdayHours);
        setOpeningHours("Wednesday", R.id.wednesdayHours);
        setOpeningHours("Thursday", R.id.thursdayHours);
        setOpeningHours("Friday", R.id.fridayHours);
        setOpeningHours("Saturday", R.id.saturdayHours);
        setOpeningHours("Sunday", R.id.sundayHours);
    }

    /**
     * Set the formatted opening hours text to the View with the given id
     * Do not do anything if there's an error (because the store can close on some daysOfWeek)
     *
     * @param daysOfWeek day of the week
     * @param id         id of the View
     */
    private void setOpeningHours(String daysOfWeek, int id) {
        HttpUtils.get("openingH/" + daysOfWeek, new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String startTime = FormatUtils.formatTime(response.getString("startTime"));
                    String endTime = FormatUtils.formatTime(response.getString("endTime"));
                    String text = startTime + " to " + endTime;
                    ((TextView) findViewById(id)).setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Generates a TableRow View object for the provided holiday.
     * This will eventually be used to populate the Holiday table.
     *
     * @param holiday a holiday JSONObject
     * @return a formatted TableRow
     * @throws JSONException if exception is thrown during JSON parsing
     */
    private TableRow createHolidayRow(JSONObject holiday) throws JSONException {
        // Format the table row
        TableRow holidayEntry = new TableRow(this);
        TableLayout.LayoutParams holidayLayout = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        holidayEntry.setLayoutParams(holidayLayout);

        // Format the holiday name column
        final TextView name = new TextView(this);
        name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        name.setGravity(Gravity.LEFT);
        name.setText(holiday.getString("name"));
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        holidayEntry.addView(name);

        // Format the holiday date column
        final TextView date = new TextView(this);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int dps = 150;
        int pixels = (int) (dps * scale + 0.5f);
        date.setLayoutParams(new TableRow.LayoutParams(pixels, TableRow.LayoutParams.WRAP_CONTENT));
        date.setGravity(Gravity.CENTER_HORIZONTAL);
        date.setText(FormatUtils.formatDate(holiday.getString("date")));
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        holidayEntry.addView(date);

        return holidayEntry;
    }
}