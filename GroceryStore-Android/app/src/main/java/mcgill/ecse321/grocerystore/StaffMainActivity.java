package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class StaffMainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);
        drawerLayout = findViewById(R.id.staff_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        MenuItem schedule = ((NavigationView) findViewById(R.id.staff_navigation)).getMenu().findItem(R.id.staff_schedule);
        if (User.getUserType().equals("Owner")) {
            schedule.setVisible(false);
        }
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}