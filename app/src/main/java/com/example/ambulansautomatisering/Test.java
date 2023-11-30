package com.example.ambulansautomatisering;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Timer;
import java.util.TimerTask;

public class Test extends HomeScreen{
    private int delay;
    public Test() {
        
    }

    void testReceipt(int delay){
        Timer timer = new Timer();
        Test test = new Test();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                test.showMessageReceived();
            }
        };
        // schedule task
        timer.schedule(task, delay);
    }


}
