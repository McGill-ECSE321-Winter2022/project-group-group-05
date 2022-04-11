package mcgill.ecse321.grocerystore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class StaffScheduleActivity extends AppCompatActivity {

    private ArrayList<LinearLayout> workSchedule;
    private int currentlyShownSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_schedule);
        workSchedule = new ArrayList<>();
        currentlyShownSchedule = 0;
        LinearLayout scheduleView = (LinearLayout) findViewById(R.id.schedule);
        HttpUtils.get("/employee/" + User.getInstance().getUsername() + "/getSchedules", new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date(System.currentTimeMillis()));
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date firstScheduledDate = Date.valueOf(response.getJSONObject(0).getString("date"));
                        Date today = new Date(calendar.getTimeInMillis());
                        if (today.after(firstScheduledDate)) {
                            calendar.setTime(firstScheduledDate);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                        }
                        int i = 0;
                        while (i < response.length() || !today.before(new Date(calendar.getTimeInMillis()))) {
                            JSONArray schedules = new JSONArray();
                            Date scheduleDate = new Date(calendar.getTimeInMillis());
                            Log.d("date", scheduleDate.toString());
                            if (today.after(scheduleDate)) {
                                currentlyShownSchedule++;
                            }
                            while (i < response.length() && scheduleDate.equals(Date.valueOf(response.getJSONObject(i).getString("date")))) {
                                schedules.put(response.getJSONObject(i));
                                i++;
                            }
                            LinearLayout scheduledDay = createScheduleLayout(scheduleDate, schedules);
                            scheduledDay.setVisibility(View.GONE);
                            workSchedule.add(scheduledDay);
                            scheduleView.addView(scheduledDay);
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                        }
                    } else {
                        LinearLayout scheduledDay = createScheduleLayout(new Date(System.currentTimeMillis()), new JSONArray());
                        scheduledDay.setVisibility(View.GONE);
                        workSchedule.add(scheduledDay);
                        scheduleView.addView(scheduledDay);
                    }
                    workSchedule.get(currentlyShownSchedule).setVisibility(View.VISIBLE);
                    findViewById(R.id.scheduled_scrollview).setOnTouchListener(new SwipeListener());
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

    public void main(View v) {
        Intent mainPage = new Intent(this, StaffMainActivity.class);
        startActivity(mainPage);
    }

    private class SwipeListener implements View.OnTouchListener {

        GestureDetector gestureDetector;

        SwipeListener() {
            int threshold = 100;
            int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    Log.d("swipe", "swiped");
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try {
                        if (Math.abs(xDiff) > Math.abs(yDiff)) {
                            if (Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold) {
                                if (xDiff > 0) {
                                    // do right swipe
                                    Log.d("swipe", "swiped right");
                                    if (currentlyShownSchedule > 0) {
                                        LinearLayout currentSchedule = workSchedule.get(currentlyShownSchedule);
                                        LinearLayout nextSchedule = workSchedule.get(currentlyShownSchedule - 1);
                                        currentSchedule.startAnimation(AnimationUtils.loadAnimation(StaffScheduleActivity.this, R.anim.slide_out_right));
                                        currentSchedule.setVisibility(View.GONE);
                                        nextSchedule.startAnimation(AnimationUtils.loadAnimation(StaffScheduleActivity.this, R.anim.slide_in_left));
                                        nextSchedule.setVisibility(View.VISIBLE);
                                        currentlyShownSchedule--;
                                    }
                                } else {
                                    // do left swipe
                                    Log.d("swipe", "swiped left");
                                    if (currentlyShownSchedule < workSchedule.size() - 1) {
                                        LinearLayout currentSchedule = workSchedule.get(currentlyShownSchedule);
                                        LinearLayout nextSchedule = workSchedule.get(currentlyShownSchedule + 1);
                                        currentSchedule.startAnimation(AnimationUtils.loadAnimation(StaffScheduleActivity.this, R.anim.slide_out_left));
                                        nextSchedule.startAnimation(AnimationUtils.loadAnimation(StaffScheduleActivity.this, R.anim.slide_in_right));
                                        currentSchedule.setVisibility(View.GONE);
                                        nextSchedule.setVisibility(View.VISIBLE);
                                        currentlyShownSchedule++;
                                    }
                                }
                            }
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            };
            gestureDetector = new GestureDetector(StaffScheduleActivity.this, listener);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("swipe", "touched");
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }

    private LinearLayout createScheduleLayout(Date date, JSONArray scheduledShifts) throws JSONException {
        // Extract the day of week and the date for the schedules to us as labels
        DateFormat dayOfWeekFormatter = new SimpleDateFormat("EEEE", Locale.CANADA);
        DateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.CANADA);
        String dayOfWeekText = dayOfWeekFormatter.format(date);
        String dateText = dateFormatter.format(date);

        LinearLayout.LayoutParams scheduleLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Create layout to hold schedule
        LinearLayout schedule = new LinearLayout(this);
        schedule.setLayoutParams(scheduleLayout);
        schedule.setOrientation(LinearLayout.VERTICAL);

        // Create label for the day of the week
        TextView dayOfWeekLabel = new TextView(this);
        dayOfWeekLabel.setLayoutParams(scheduleLayout);
        dayOfWeekLabel.setGravity(Gravity.CENTER);
        dayOfWeekLabel.setPadding(10, 10, 10, 0);
        dayOfWeekLabel.setBackgroundColor(getResources().getColor(R.color.grocery));
        dayOfWeekLabel.setTextColor(getResources().getColor(R.color.white));
        dayOfWeekLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        dayOfWeekLabel.setText(dayOfWeekText);

        // Create label for the date
        TextView dateLabel = new TextView(this);
        dateLabel.setLayoutParams(scheduleLayout);
        dateLabel.setGravity(Gravity.CENTER);
        dateLabel.setPadding(10, 0, 10, 10);
        dateLabel.setBackgroundColor(getResources().getColor(R.color.grocery));
        dateLabel.setTextColor(getResources().getColor(R.color.white));
        dateLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        dateLabel.setText(dateText);

        // add labels to schedule
        schedule.addView(dayOfWeekLabel);
        schedule.addView(dateLabel);

        // add an entry to the schedule for each shift
        for (int i = 0; i < scheduledShifts.length(); i++) {
            // Create layout to hold shift card
            LinearLayout shift = new LinearLayout(this);
            shift.setLayoutParams(scheduleLayout);
            shift.setPadding(30, 30, 30, 30);
            shift.setBackgroundColor(getResources().getColor(R.color.grey));
            shift.setOrientation(LinearLayout.VERTICAL);

            // Create label for the name of the shift
            TextView shiftName = new TextView(this);
            shiftName.setLayoutParams(scheduleLayout);
            shiftName.setGravity(Gravity.CENTER);
            shiftName.setPadding(20, 20, 20, 20);
            shiftName.setBackgroundColor(getResources().getColor(R.color.grocery));
            shiftName.setTextColor(getResources().getColor(R.color.white));
            shiftName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            shiftName.setText(scheduledShifts.getJSONObject(i).getJSONObject("shift").getString("name"));

            // Create label for start time
            TextView startTime = new TextView(this);
            startTime.setLayoutParams(scheduleLayout);
            startTime.setGravity(Gravity.CENTER);
            startTime.setPadding(20, 20, 20, 0);
            startTime.setBackgroundColor(getResources().getColor(R.color.white));
            startTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            String startTimeText = "Start Time: " + formatTime(scheduledShifts.getJSONObject(i).getJSONObject("shift").getString("startTime"));
            startTime.setText(startTimeText);

            // Create label for end time
            TextView endTime = new TextView(this);
            endTime.setLayoutParams(scheduleLayout);
            endTime.setGravity(Gravity.CENTER);
            endTime.setPadding(20, 0, 20, 20);
            endTime.setBackgroundColor(getResources().getColor(R.color.white));
            endTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            String endTimeText = "End Time: " + formatTime(scheduledShifts.getJSONObject(i).getJSONObject("shift").getString("endTime"));
            endTime.setText(endTimeText);

            // add labels to shift card
            shift.addView(shiftName);
            shift.addView(startTime);
            shift.addView(endTime);

            schedule.addView(shift);
        }

        // if there are no shifts scheduled for this date, add a label indicating so
        if (scheduledShifts.length() == 0) {
            TextView noShiftLabel = new TextView(this);
            noShiftLabel.setLayoutParams(scheduleLayout);
            noShiftLabel.setGravity(Gravity.CENTER);
            noShiftLabel.setPadding(20, 20, 20, 20);
            noShiftLabel.setBackgroundColor(getResources().getColor(R.color.grocery));
            noShiftLabel.setTextColor(getResources().getColor(R.color.white));
            noShiftLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            String noShiftText = "No shifts scheduled for this date!";
            noShiftLabel.setText(noShiftText);

            schedule.addView(noShiftLabel);
        }

        return schedule;
    }

    private String formatTime(String time) {
        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]) % 12;
        hour += hour == 0 ? 12 : 0;
        String amOrPm = Integer.parseInt(timeComponents[0]) < 12 ? " AM" : " PM";
        return hour + ":" + timeComponents[1] + amOrPm;
    }
}