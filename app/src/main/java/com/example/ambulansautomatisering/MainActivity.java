package com.example.ambulansautomatisering;

import android.Manifest;
import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationHelper locationHelper;

    private double[] hospital_pos = {1.0, 1.0};             // läs in från terminal????

    private double[] patient_position = {6.8155, -5.2549};  // läs in från terminal????


    protected Boolean checkPosition(double[] current_pos, double[] goal_pos){
        return (current_pos[0] == goal_pos[0]) && (current_pos[1] == goal_pos[1]);
    }

    protected double roundCoordinate(){
        return 2.0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHelper = new LocationHelper(this, this);

        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission already granted, proceed with getting location
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

        double[] current = {Math.round(latitude*10000)/10000, Math.round(longitude*10000)/10000};

        double[] target = {Math.round(patient_position[0]*10000)/10000, Math.round(patient_position[1]*10000)/10000};
        if(checkPosition(current, target)){
            String locationText = "GOOOOOOOOOOL";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }
        // Update the TextView with the new location
        /*
        String locationText = "Latitude: " + latitude + "\nLongitude: " + longitude;
        TextView locationTextView = findViewById(R.id.locationTextView);
        locationTextView.setText(locationText);

         */
    }
}
