package com.example.ambulansautomatisering;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationHelper locationHelper;
    private Date dt_overl;
    private Date dt_adress;

    /* create new task when pressing "kvittera" (button not implemented yet). Should be a maximum of 2 tasks */
    private Task task1 = new Task(1);

    private final Coordinate ambulance_station = new Coordinate(57.7056, 11.8876); // Ruskvädersgatan 10, 418 34 Göteborg, Sweden
    private final Coordinate hospital_pos = new Coordinate(57.7219, 12.0498); // östra sjukhuset
    // patient pos == null island 10
    //private double[] patient_position = {6.8155, -5.2549};  // Read from terminal to simulate message from SOS?

    private final Coordinate patient_position = new Coordinate(57.6814, 11.9105); // Sven Brolids Väg 9



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
        timeStampManager = new TimeStampManager(buttons);
        locationHelper = new LocationHelper(this, this);

        // Makes our "tidsnotera" button clickable (links function onClick() below to onCLick event)
        Button buttonSetTime = findViewById(R.id.buttonSetTime);
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets the time of the first non-empty timestamp
                // Get current date, time and time zone.
                java.util.Date currentDate = new java.util.Date();
                timeStampManager.setTime(currentDate);
            }
        });


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location and starting accelerometer updates
            locationHelper.startLocationUpdates();
        }
    }

    private boolean isLocationOutsideThreshold(Coordinate current,
                                               Coordinate target, float threshold) {
        float[] results = new float[1];
        Location.distanceBetween(current.getLatitude(), current.getLongitude(), target.getLatitude(), target.getLongitude(), results);
        float distanceInMeters = results[0];
        return distanceInMeters > threshold;
    }


    @Override
    // This is when activity is reopened
    protected void onResume() {
        super.onResume();
        locationHelper.startLocationUpdates();
    }



    /* Remove? since we want to use the app in the back ground. */
    @Override
    // This is when activity loses focus
    protected void onPause() {
        super.onPause();
        locationHelper.stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle the updated location here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Coordinate current = new Coordinate(latitude, longitude);

        // Get current date, time and time zone.
        java.util.Date currentDate = new java.util.Date();
        // Format the date without including the time zone (to match the excel file Andreas sent)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);


        /* Location outside 100m? then we've left ambulance station, this time stamp may be redundant.
         change to "kvittering"? */
        if(isLocationOutsideThreshold(current, ambulance_station, 100) && timeStampManager.isTimeStampChecked(0)) { /* Check if this is the correct time stamp*/
            timeStampManager.setTime(0, currentDate);
            // Update the TextView with the new location
            String locationText = "Left station";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 2 */
        else if(!isLocationOutsideThreshold(current, patient_position, 100) && timeStampManager.isTimeStampChecked(1)) {
            timeStampManager.setTime(1, currentDate);
            // Update the TextView with the new location
            String locationText = "Arrived at patient address";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 5 */
        else if(!isLocationOutsideThreshold(current, hospital_pos, 100) && timeStampManager.isTimeStampChecked(4)){
            // Save in external excel/txt?
            timeStampManager.setTime(4, currentDate);
            // Update the TextView with the new location
            String locationText = "Arrived at hospital";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }


    }
}
