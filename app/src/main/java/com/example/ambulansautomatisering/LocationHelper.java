package com.example.ambulansautomatisering;

import android.content.Context;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class LocationHelper {

    // From google play services
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationListener locationListener;

    public interface LocationListener {
        void onLocationChanged(Location location);
    }

    public LocationHelper(Context context, LocationListener listener) {
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        locationListener = listener;
        createLocationCallback();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (locationListener != null) {
                        locationListener.onLocationChanged(location);
                    }
                }
            }
        };
    }

    public void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000); // Fastest update interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null /* Looper */
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
