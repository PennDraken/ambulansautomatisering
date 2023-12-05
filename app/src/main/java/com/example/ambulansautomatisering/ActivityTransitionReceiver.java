package com.example.ambulansautomatisering;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;

public class ActivityTransitionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                // Do something useful here...
                Log.d("asd", ActivityTransitionUtil.toActivityString(event.getActivityType())
                        + " - " + ActivityTransitionUtil.toTransitionType(event.getTransitionType()));
            }
        }
    }
}
