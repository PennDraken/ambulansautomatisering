package com.example.ambulansautomatisering;

import android.app.TimePickerDialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationHelper locationHelper;
    private Date dt_overl;
    private Date dt_adress;

    // private final Tuple ambulance_station = new Tuple(57.7056, 11.8876); // Ruskvädersgatan 10, 418 34 Göteborg, Sweden
    private Tuple ambulance_station = null; // This location is set at startup
    private final Tuple hospital_pos = new Tuple(57.722, 12.0498); // östra sjukhuset
    private final Tuple patient_position = new Tuple(57.723, 12.0227); // Ullevi
    private List<Tuple> walkingTimeList = new ArrayList<>();


    private TimeStampManager timeStampManager; // handles our timestamps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // declares our timeStampManager
        // we need to input our buttons we want to link as an array
        // so that the manager updates the correct UI elements
        Button[] buttons = new Button[6];
        buttons[0] = findViewById(R.id.buttonTimeStamp1);
        buttons[1] = findViewById(R.id.buttonTimeStamp2);
        buttons[2] = findViewById(R.id.buttonTimeStamp3);
        buttons[3] = findViewById(R.id.buttonTimeStamp4);
        buttons[4] = findViewById(R.id.buttonTimeStamp5);
        buttons[5] = findViewById(R.id.buttonTimeStamp6);
        Button saveButton = findViewById(R.id.buttonSetTime);
        timeStampManager = new TimeStampManager(this, buttons, saveButton);
        locationHelper = new LocationHelper(this, this);

        // This is so the user can change the times of events (in case they were registered wrong)
        for (int i=0;i<buttons.length;i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // on below line we are getting the
                    // instance of our calendar.
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    // on below line we are initializing our Time Picker Dialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    java.util.Date currentDate = new java.util.Date();
                                    currentDate.setHours(hourOfDay);
                                    currentDate.setMinutes(minute);
                                    timeStampManager.setTime(index, currentDate);
                                }
                            }, hour, minute, false);
                    timePickerDialog.show();
                }
            });
        }
        // we've "kvitterat" now set first time stamp.
        // java.util.Date currentDate = new java.util.Date();
        // timeStampManager.setTime(0, currentDate);
        // Makes our "tidsnotera" button clickable (links function onClick() below to onCLick event)
        Button buttonSetTime = findViewById(R.id.buttonSetTime);
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets the time of the first non-empty timestamp
                // Get current date, time and time zone.
                java.util.Date currentDate = new Date();
                timeStampManager.setTime(5,currentDate);
                timeStampManager.complete(new Date());
                completeMission();
            }
        });
        buttonSetTime.setEnabled(false);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location and starting accelerometer updates
            locationHelper.startLocationUpdates();
        }
    }

    // Gets how many seconds has passed since probable time arriving at patient
    // List contains tuples of type (Date date, float speed)
    public static Date findTimeArrivalAtPatient(List<Tuple> timeArray) {
        long timeEnteredIdleSeconds = 0;
        Date probableDate = (Date)timeArray.get(0).getA();
        long maxTimeIdle = 0;
        // remove duplicate speed=0
        for (int prev_i = 0; prev_i < timeArray.size() - 1; prev_i++) {
            Tuple prev_tuple = timeArray.get(prev_i);
            float prev_speed = (float) prev_tuple.getB();
            // Check if the current speed is 0
            if (prev_speed == 0) {
                // Iterate through subsequent elements with speed 0 and remove them
                for (int next_i = prev_i + 1; next_i < timeArray.size(); next_i++) {
                    Tuple next_tuple = timeArray.get(next_i);
                    float next_speed = (float) next_tuple.getB();
                    // Check if the next speed is also 0
                    if (next_speed == 0) {
                        // Remove the element with speed 0
                        timeArray.remove(next_i);
                        // Decrement next_i to account for the removed element
                        next_i--;
                    } else {
                        // If the next speed is not 0, break the loop
                        break;
                    }
                }
            }
        }

        // We need to find the value with the longest time spent idle
        for (int prev_i = 0; prev_i < timeArray.size() - 1; prev_i++) {
            Tuple prev_tuple = timeArray.get(prev_i);
            Date prev_date = (Date) prev_tuple.getA();
            float prev_speed = (float) prev_tuple.getB();

            Tuple curr_tuple = timeArray.get(prev_i + 1); // Get the next tuple
            Date curr_date = (Date) curr_tuple.getA();
            float curr_speed = (float) curr_tuple.getB();

            long deltaTime = (curr_date.getTime() - prev_date.getTime()) / 1000; // Convert milliseconds to seconds

            if (prev_speed == 0 && deltaTime > maxTimeIdle) {
                maxTimeIdle = deltaTime;
                timeEnteredIdleSeconds = prev_date.getTime() / 1000; // Convert milliseconds to seconds
                probableDate = prev_date;
            }
        }
        return probableDate;
    }

    private boolean isLocationOutsideThreshold(Tuple current,
                                               Tuple target, float threshold) {
        float[] results = new float[1];
        Location.distanceBetween((double) current.getA(), (double)current.getB(), (double)target.getA(), (double)target.getB(), results);
        float distanceInMeters = results[0];
        return distanceInMeters > threshold;
    }

    @Override
    // This is when activity is reopened
    protected void onResume() {
        super.onResume();
        locationHelper.startLocationUpdates();
    }

    public void completeMission() {
        Intent intent = new Intent();
        intent.putExtra("MissionData", timeStampManager);
        setResult(RESULT_OK, intent);
        finish(); // Go back to home-screen activity
    }

    /* Remove? since we want to use the app in the back ground. */
    @Override
    // This is when activity loses focus
    protected void onPause() {
        super.onPause();
        locationHelper.stopLocationUpdates();
    }

    // onLocationChanged gets run at startup
    @Override
    public void onLocationChanged(Location location) {
        float speed = location.getSpeed(); // get speed
        // Handle the updated location here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Tuple current = new Tuple(latitude, longitude);

        // Get current date, time and time zone.
        java.util.Date currentDate = new java.util.Date();
        // Format the date without including the time zone (to match the excel file Andreas sent)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        // This is to initialize our starting position
        if (ambulance_station == null) {
            ambulance_station = new Tuple(latitude, longitude);
            return; // Early return
        }

        String locationTex = "Hastighet: " + speed;
        TextView locationTextVie = findViewById(R.id.locationTextView);
        locationTextVie.setText(locationTex);

        /* Time stamp 0 */
        if(isLocationOutsideThreshold(current, ambulance_station, 15) && !timeStampManager.isTimeStampChecked(0)) {
            timeStampManager.setTime(0, currentDate);
            // Update the TextView with the new location
            String locationText = "På väg mot hämtplats";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 1 */
        if(!isLocationOutsideThreshold(current, patient_position, 50) && speed==0 && !timeStampManager.isTimeStampChecked(1) && timeStampManager.isTimeStampChecked(0)) {
            timeStampManager.setTime(1, currentDate);
            // Update the TextView with the new location
            String locationText = "Framme hos patientaddress";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 2 (walking to patient)*/
        if(speed < 6 && !timeStampManager.isTimeStampChecked(2) && timeStampManager.isTimeStampChecked(1)) {
            // Calculate probable patient meetup
            Tuple walkingData = new Tuple(new Date(), speed);
            walkingTimeList.add(walkingData);
        }

        /* Time stamp 2 & 3 */
        else if(isLocationOutsideThreshold(current, patient_position, 10) && speed >= 6 && !timeStampManager.isTimeStampChecked(3) && timeStampManager.isTimeStampChecked(1) /* ändra till index 2*/ ) {
            timeStampManager.setTime(3, currentDate);
            Date arrivedPatient = findTimeArrivalAtPatient(walkingTimeList);
            timeStampManager.setTime(2, arrivedPatient);
            // Update the TextView with the new location
            String locationText = "Lämnat patientaddress";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 4 */
        else if(!isLocationOutsideThreshold(current, hospital_pos, 50) && !timeStampManager.isTimeStampChecked(4) && timeStampManager.isTimeStampChecked(3)){
            Button buttonSetTime = findViewById(R.id.buttonSetTime);
            buttonSetTime.setEnabled(true); // Note: this only gets updated if GPS fills in the last coordinate
            // Save in external excel/txt?
            timeStampManager.setTime(4, currentDate);
            // Update the TextView with the new location
            String locationText = "Framme vid sjukhus";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }
    }
}
