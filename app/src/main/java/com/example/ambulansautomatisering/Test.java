package com.example.ambulansautomatisering;

import java.util.Timer;
import java.util.TimerTask;

public class Test {
    public Test() {

    }

    public void testReceipt(int delay){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Create an instance of HomeScreen to call showMessageReceived()
                HomeScreen homeScreen = new HomeScreen();
                homeScreen.showMessageReceived();
            }
        };
        // schedule task
        timer.schedule(task, delay);
    }
}
