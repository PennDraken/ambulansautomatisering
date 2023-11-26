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

    private final Coordinate patient_position = new Coordinate(6.8155, -5.2549);


    private boolean isLocationOutsideThreshold(Coordinate current,
                                               Coordinate target, float threshold) {
        float[] results = new float[1];
        Location.distanceBetween(current.getLatitude(), current.getLongitude(), target.getLatitude(), target.getLongitude(), results);
        float distanceInMeters = results[0];
        return distanceInMeters > threshold;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this, this);

        // Makes our "tidsnotera" button clickable (links function onClick() to onCLick event)
        Button buttonSetTime = findViewById(R.id.buttonSetTime);
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to be executed when the button is clicked
                // at the moment it just updates the text of the button

                // Get current date, time and time zone.
                java.util.Date currentDate = new java.util.Date();
                // Format the date and time without including the time zone
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                String current_time = sdf.format(currentDate);
                buttonSetTime.setText(current_time);
                // The code below is to reset the button text.
                buttonSetTime.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonSetTime.setText("Visa nuvarande tid och datum: ");
                    }
                }, 2000); // Adjust the delay as needed
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

    // This function is called when the user presses the "Notera tid" button
    public void noteTimeClick() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        // Display current time
        Log.d("CurrentTime", "Current Time: " + currentTime);
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
        if(isLocationOutsideThreshold(current, ambulance_station, 100) /* Check if this is the correct time stamp*/){
            // lägg till tidsnotering_n i lista?

            // Update the TextView with the new location
            String locationText = "Left station";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 2 */
        if(!isLocationOutsideThreshold(current, patient_position, 100) && task1.getTS_checked(2)){
            // Save in external excel/txt?
            dt_adress = currentDate;


            // Update the TextView with the new location
            String locationText = "Arrived at patient address";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 5 */
        if(!isLocationOutsideThreshold(current, hospital_pos, 100) && task1.getTS_checked(5)){
            // Save in external excel/txt?
            dt_overl = currentDate;


            // Update the TextView with the new location
            String locationText = "Arrived at hospital";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }


    }
}
