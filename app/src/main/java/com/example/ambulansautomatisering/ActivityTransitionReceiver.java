package com.example.ambulansautomatisering;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityTransitionReceiver extends BroadcastReceiver {
    private long timeStarted = System.currentTimeMillis();
    public List<Tuple> timeArray = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent){
        // This is called when user has changed activity type
        if (ActivityTransitionResult.hasResult(intent) && !MainActivity.timeStampManager.isTimeStampChecked(3)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // Do something useful here...
                Log.d("asd", ActivityTransitionUtil.toActivityString(event.getActivityType())
                        + " - " + ActivityTransitionUtil.toTransitionType(event.getTransitionType()));
                try {
                    MainActivity.getInstace().updateTheTextView(ActivityTransitionUtil.toActivityString(event.getActivityType()));
                    timeArray.add(new Tuple(System.currentTimeMillis() - timeStarted, ActivityTransitionUtil.toActivityString(event.getActivityType())));
                } catch (Exception e) {

                }
                // Update estimated time of meeting patient
                if (timeArray.size()!=0) {
                    // Get old date from timeStamp 1 (arrived at patient address) and add seconds to find estimated walk time
                    Date currentDate = MainActivity.timeStampManager.getTime(1);
                    long seconds = MainActivity.getSeconds(timeArray);
                    currentDate.setTime(currentDate.getTime() + seconds);
                    MainActivity.timeStampManager.setTime(2, currentDate);
                }

                // Check if driving and has walked (we are probably driving away from the patient address)
                if (ActivityTransitionUtil.toActivityString(event.getActivityType()) == "IN_VEHICLE") {
                    // Update left patient address to current time
                    MainActivity.timeStampManager.setTime(3, new java.util.Date());
                }
            }
        }
    }


}
