package com.example.ambulansautomatisering;

import android.Manifest;
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
    private TextView yTextView;

    private double[] hospital_pos = {1.0, 1.0};             // läs in från terminal????

    // patient pos == null island 10
    private double[] patient_position = {6.8155, -5.2549};  // läs in från terminal????


    protected Boolean checkPosition(double[] current_pos, double[] goal_pos){
        return (current_pos[0] == goal_pos[0]) && (current_pos[1] == goal_pos[1]);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yTextView = findViewById(R.id.yTextView);

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
        /*
        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location
            locationHelper.startLocationUpdates();
        }

         */
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

        double[] current = {Math.round(latitude*10000)/10000, Math.round(longitude*10000)/10000};

        double[] target = {Math.round(patient_position[0]*10000)/10000, Math.round(patient_position[1]*10000)/10000};

        if(checkPosition(current, target)){
            // Update the TextView with the new location
            String locationText = "GOOOOOOOOOOL";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        else {
            // Update the TextView with the new location
            String locationText = "On the way to patient";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float y = event.values[1];

            // Update TextViews or perform other actions with accelerometer data
            yTextView.setText("Y: " + y);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // syntaxerror if not added
    }
}
