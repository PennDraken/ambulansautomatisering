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


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener, SensorEventListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationHelper locationHelper;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private TextView accTextView;


    private final Tuple ambulance_station = new Tuple(57.7056, 11.8876); // Ruskvädersgatan 10, 418 34 Göteborg, Sweden
    private final Tuple hospital_pos = new Tuple(57.7219, 12.0498); // östra sjukhuset
    // patient pos == null island 10
    //private double[] patient_position = {6.8155, -5.2549};  // Read from terminal to simulate message from SOS?

    private final Tuple patient_position = new Tuple(6.8155, -5.2549);

    private boolean isLocationOutsideThreshold(Tuple current,
                                               Tuple target, float threshold) {
        float[] results = new float[1];
        Location.distanceBetween(current.getLatitude(), current.getLongitude(), target.getLatitude(), target.getLongitude(), results);
        float distanceInMeters = results[0];
        return distanceInMeters > threshold;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accTextView = findViewById(R.id.accTextView);;
        locationHelper = new LocationHelper(this, this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location and starting accelerometer updates
            locationHelper.startLocationUpdates();
            if (accelerometerSensor != null) {
                sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    // This is when activity is reopened
    protected void onResume() {
        super.onResume();
        locationHelper.startLocationUpdates();

        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    // This is when activity loses focus
    protected void onPause() {
        super.onPause();
        locationHelper.stopLocationUpdates();

        if (accelerometerSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle the updated location here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Tuple current = new Tuple(latitude, longitude);

        Tuple patient = new Tuple(patient_position.getLatitude(), patient_position.getLongitude());

        // Location outside 100m? then we've left hospital
        if(isLocationOutsideThreshold(current, ambulance_station, 100)){
            // lägg till tidsnotering_n i lista?

            // Update the TextView with the new location
            String locationText = "Left station";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        if(!isLocationOutsideThreshold(current, hospital_pos, 100) /*Also check if this is the correct time stamp*/ ){
            // lägg till tidsnotering_n i lista?

            // Update the TextView with the new location
            String locationText = "Arrived at hospital";
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];


            double total_acc = Math.sqrt(x*x+y*y+z*z);

            // Update TextViews or perform other actions with accelerometer data

            accTextView.setText("Acceleration: " + total_acc);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // syntaxerror if not added
    }
}
