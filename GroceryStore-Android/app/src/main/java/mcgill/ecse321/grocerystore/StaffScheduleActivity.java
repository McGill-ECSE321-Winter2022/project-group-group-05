package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class StaffScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule);
    }

    public void main(View v) {
        Intent mainPage = new Intent(this, StaffMainActivity.class);
        startActivity(mainPage);
    }
}