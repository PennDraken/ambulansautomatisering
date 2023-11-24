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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationHelper locationHelper;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private TextView accTextView;

    public List<Task> tasks = new ArrayList<Task>();


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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location and starting accelerometer updates
            locationHelper.startLocationUpdates();
        }
    }

    @Override
    // This is when activity is reopened
    protected void onResume() {
        super.onResume();
        locationHelper.startLocationUpdates();
    }

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

        Coordinate patient = new Coordinate(patient_position.getLatitude(), patient_position.getLongitude());

        // Location outside 100m? then we've left hospital
        if(isLocationOutsideThreshold(current, ambulance_station, 100) /* Check if this is the correct time stamp*/){
            // lägg till tidsnotering_n i lista?

            // Get the current time
            long currentTimeMillis = System.currentTimeMillis();
            // Convert the time to a Date object if needed
            java.util.Date currentDate = new java.util.Date(currentTimeMillis);

            // Update the TextView with the new location
            String locationText = "Left station";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        if(!isLocationOutsideThreshold(current, hospital_pos, 100) /*Also check if this is the correct time stamp*/ ){
            // lägg till tidsnotering_n i lista?

            // Get the current time
            long currentTimeMillis = System.currentTimeMillis();
            // Convert the time to a Date object if needed
            java.util.Date currentDate = new java.util.Date(currentTimeMillis);

            // Update the TextView with the new location
            String locationText = "Arrived at hospital";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        if(!isLocationOutsideThreshold(current, patient_position, 100) /*Also check if this is the correct time stamp*/){
            // lägg till tidsnotering_n i lista?

            // Get the current time
            long currentTimeMillis = System.currentTimeMillis();
            // Convert the time to a Date object if needed
            java.util.Date currentDate = new java.util.Date(currentTimeMillis);

            // Update the TextView with the new location
            String locationText = "Arrived at patient address";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }
        /*
        else {
            // Update the TextView with the new location
            String locationText = "Idling";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

         */
    }
}
