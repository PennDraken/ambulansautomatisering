package com.example.ambulansautomatisering;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.TimePicker;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener, EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final int REQUEST_ACTIVITY_TRANSITION = 123;
    public static final int REQUEST_ACTIVITY_TRANSITION_RECEIVER = 166;
    private LocationHelper locationHelper;

    private Date dt_overl;
    private Date dt_adress;

    private final Tuple ambulance_station = new Tuple(57.7056, 11.8876); // Ruskvädersgatan 10, 418 34 Göteborg, Sweden
    private final Tuple hospital_pos = new Tuple(57.7219, 12.0498); // östra sjukhuset
    // patient pos == null island 10
    //private double[] patient_position = {6.8155, -5.2549};  // Read from terminal to simulate message from SOS?

    private final Tuple patient_position = new Tuple(57.6814, 11.9105); // Sven Brolids Väg 9

    private TimeStampManager timeStampManager; // handles our timestamps
    private Switch simpleSwitch;
    private TextView test;
    private TextView test2;
    private ActivityRecognitionClient client;
    private static MainActivity ins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ins = this;

        client = ActivityRecognition.getClient(this);
        test = findViewById(R.id.txt_activity);

        simpleSwitch = findViewById(R.id.simpleSwitch);
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasPerm(MainActivity.this)){
                        test.setText("no permission!");
                        simpleSwitch.setChecked(false);
                        requestActivityTransitionPermission();
                    } else {
                        test.setText("permission!");
                        requestForUpdates();
                    }
                } else {
                    test.setText("inte checked");
                    removeUpdates();
                }
            }
        });

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

        // This is so the user can change the times of events (in case they were registered wrong)
        for (int i = 0; i < buttons.length; i++) {
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


        // Makes our "tidsnotera" button clickable (links function onClick() below to onCLick event)
        Button buttonSetTime = findViewById(R.id.buttonSetTime);
        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sets the time of the first non-empty timestamp
                // Get current date, time and time zone.
                java.util.Date currentDate = new java.util.Date();
                timeStampManager.setTime(5, currentDate);
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

        testGetSeconds();
    }
    public static MainActivity  getInstace(){
        return ins;
    }
    public void updateTheTextView(final String t) {

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                TextView textV1 = (TextView) findViewById(R.id.txt_confidence);
                textV1.setText(t);
            }
        });
    }

    // Gets how many seconds has passed since activity started
    public long getSeconds(List<Tuple> timeArray) {
        long timeEnteredIdle = 0;
        long maxTimeIdle = 0;
        // We need to find the value with the longest time spent idle
        for (int prev_i = 0; prev_i < timeArray.size()-1; prev_i++) {
            Tuple prev_tuple = timeArray.get(prev_i);
            long prev_time = (long) prev_tuple.getA();
            for (int curr_i = 0; curr_i < timeArray.size(); curr_i++) {
                // We go through all elements and find the value with the longest timespan spent still
                Tuple curr_tuple = timeArray.get(curr_i);
                String curr_activity = (String) curr_tuple.getA();
                long curr_time = (long) curr_tuple.getB();
                long deltaTime = curr_time - prev_time;
                if (curr_activity == "STILL" && deltaTime > maxTimeIdle) {
                    maxTimeIdle = deltaTime;
                    timeEnteredIdle = curr_time;
                }
            }
        }
        return timeEnteredIdle;
    }

    public Boolean testGetSeconds() {
        List<Tuple> testTuple = new ArrayList<>();
        testTuple.add(new Tuple(0, "WALKING"));
        testTuple.add(new Tuple(45, "WALKING"));
        testTuple.add(new Tuple(150, "IDLE"));
        testTuple.add(new Tuple(30, "WALKING"));
        testTuple.add(new Tuple(900, "IDLE"));
        testTuple.add(new Tuple(1100, "WALKING"));
        testTuple.add(new Tuple(1500, "IDLE"));
        testTuple.add(new Tuple(1700, "WALKING"));
        long time = getSeconds(testTuple);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestActivityTransitionPermission(){
        EasyPermissions.requestPermissions(
                this,
                "You need to allow activity transisiton permissions in order to use this feature",
                REQUEST_ACTIVITY_TRANSITION,
                Manifest.permission.ACTIVITY_RECOGNITION
        );
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms){
        simpleSwitch.setChecked(true);
        requestForUpdates();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms){
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            requestActivityTransitionPermission();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean hasPerm(Context context) {
        return EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACTIVITY_RECOGNITION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void requestForUpdates() {
        client.requestActivityTransitionUpdates(
                        getTransitionRequest(),
                        getPendingIntent()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("asd" ,"Success - requesting updates");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("asd" ,"Failure - requesting updates");
                    }
                });
    }

    private void removeUpdates(){
        client.removeActivityTransitionUpdates(getPendingIntent());
    }
    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(this, ActivityTransitionReceiver.class);
        return PendingIntent.getBroadcast(
                this,
                REQUEST_ACTIVITY_TRANSITION_RECEIVER,
                intent,
                PendingIntent.FLAG_IMMUTABLE        );
    }
    ActivityTransitionRequest getTransitionRequest() {
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }

    private boolean isLocationOutsideThreshold(Tuple current,
                                               Tuple target, float threshold) {
        float[] results = new float[1];
        Location.distanceBetween((double) current.getA(), (double) current.getB(), (double) target.getA(), (double) target.getB(), results);
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

        Tuple current = new Tuple(latitude, longitude);

        // Get current date, time and time zone.
        java.util.Date currentDate = new java.util.Date();
        // Format the date without including the time zone (to match the excel file Andreas sent)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);


        /* Location outside 100m? then we've left ambulance station, this time stamp may be redundant.
         change to "kvittering"? */
        /* Time stamp 0 */
        if (isLocationOutsideThreshold(current, ambulance_station, 100) && !timeStampManager.isTimeStampChecked(0)) { /* Check if this is the correct time stamp*/
            timeStampManager.setTime(0, currentDate);
            // Update the TextView with the new location
            String locationText = "Left station";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 1 */
        else if (!isLocationOutsideThreshold(current, patient_position, 100) && !timeStampManager.isTimeStampChecked(1) && timeStampManager.isTimeStampChecked(0)) {
            timeStampManager.setTime(1, currentDate);
            // Update the TextView with the new location
            String locationText = "Arrived at patient address";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 3 */
        else if (isLocationOutsideThreshold(current, patient_position, 100) && !timeStampManager.isTimeStampChecked(3) && timeStampManager.isTimeStampChecked(1) /* ändra till index 2*/) {
            timeStampManager.setTime(3, currentDate);
            // Update the TextView with the new location
            String locationText = "Left patient address";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }

        /* Time stamp 4 */
        else if (!isLocationOutsideThreshold(current, hospital_pos, 100) && !timeStampManager.isTimeStampChecked(4) && timeStampManager.isTimeStampChecked(3)) {
            Button buttonSetTime = findViewById(R.id.buttonSetTime);
            buttonSetTime.setEnabled(true);

            // Save in external excel/txt?
            timeStampManager.setTime(4, currentDate);
            // Update the TextView with the new location
            String locationText = "Arrived at hospital";
            TextView locationTextView = findViewById(R.id.locationTextView);
            locationTextView.setText(locationText);
        }
    }
}
