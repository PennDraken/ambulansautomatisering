package com.example.ambulansautomatisering;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

import java.util.ArrayList;
import java.util.List;

public class ActivityTransitionReceiver extends BroadcastReceiver {
    private long timeStarted = System.currentTimeMillis();
    public List<Tuple> timeArray = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent){
        // This is called when user is walking
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // Do something useful here...
                Log.d("asd", ActivityTransitionUtil.toActivityString(event.getActivityType())
                        + " - " + ActivityTransitionUtil.toTransitionType(event.getTransitionType()));
                try {
                    MainActivity.getInstace().updateTheTextView(ActivityTransitionUtil.toActivityString(event.getActivityType()));
                    timeArray.add(new Tuple(System.currentTimeMillis()-timeStarted, ActivityTransitionUtil.toActivityString(event.getActivityType())));
                } catch (Exception e) {

                }
            }
        }
    }


}
