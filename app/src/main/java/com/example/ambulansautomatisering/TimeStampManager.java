package com.example.ambulansautomatisering;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

// This class handles setting our timestamps
// Used to abstract away the implementation of handling timestamps
public class TimeStampManager {
    // Stores the dates and times of our different time stamps
    private Date[] timeStamps = new Date[6];
    private Button[] buttons;


    // Constructor (insert buttons which you want to set)
    public TimeStampManager(Button[] buttons) {
        this.buttons = buttons;
    }

    // Sets the most recent undefined timeStamp text to current time
    public void setTime(Date timeToSet) {
        for (int i=0;i<timeStamps.length;i++) {
            Date date = timeStamps[i];
            if (date == null) {
                timeStamps[i] = timeToSet;
                break; // Break because we set the time wanted to set
            }
        }
        updateUI();
    }

    public Date getTime(int timeStampIndex) {
        return this.timeStamps[timeStampIndex];
    }

    // Sets a specific timeStamp to a time
    public void setTime(int timeStampIndex, Date timeToSet) {
        timeStamps[timeStampIndex] = timeToSet;
        updateUI();
    }

    // Updates the UI so text corresponds to our timeStamps
    public void updateUI() {
        for (int i=0;i<timeStamps.length;i++) {
            Date date = timeStamps[i];
            if (date!=null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String current_time = sdf.format(date);
                buttons[i].setText(current_time);
            } else {
                buttons[i].setText("--:--:--");
            }
        }
    }

    // Checks if a time stamp has been filled in
    public Boolean isTimeStampChecked(int index) {
        return timeStamps[index] != null;
    }

    // Resets all timeStamps and updates UI
    public void reset() {
        this.timeStamps = new Date[6];
    }

    // Saves the dates to a file and resets the UI
    public void save() {
        // TODO not implemented yet
    }
}
