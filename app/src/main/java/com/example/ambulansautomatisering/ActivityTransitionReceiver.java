package com.example.ambulansautomatisering;


import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER;
import static com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityTransitionReceiver extends BroadcastReceiver {
    public static final int WALKING = 3;

    @Override
    public void onReceive(Context context, Intent intent){

        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                if(event.getActivityType() == WALKING){

                    //atm vid start, om den går till transition exit innan enter så
                    //hinner inte startTime sättas

                    if(event.getTransitionType() == ACTIVITY_TRANSITION_ENTER){
                        //"send" methods to the main instance
                        MainActivity.getInstace().setStartTime();
                    } else if (event.getTransitionType() == ACTIVITY_TRANSITION_EXIT) {
                        MainActivity.getInstace().setEndTime();
                        //get total standing still time
                        long totalTime = MainActivity.getInstace().getStandingStillTime();
                        MainActivity.getInstace().updateTimeText("Seconds still: " + totalTime );
                    }
                }

                //view the activity on UI
                try {
                    MainActivity.getInstace().updateActivityText(ActivityTransitionUtil.toActivityString(event.getActivityType()));
                } catch (Exception e) {

                }
            }
        }
    }
}
