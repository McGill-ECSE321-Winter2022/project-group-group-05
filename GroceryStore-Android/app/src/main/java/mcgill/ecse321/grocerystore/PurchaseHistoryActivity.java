package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class PurchaseHistoryActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, CustomerMainActivity.class);
        startActivity(mainPage);
    }
}