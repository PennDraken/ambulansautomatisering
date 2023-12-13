package com.example.ambulansautomatisering;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.BooleanSupplier;

import androidx.appcompat.app.AppCompatActivity;

// This class handles setting our timestamps
// Used to abstract away the implementation of handling timestamps
public class TimeStampManager {
    // Stores the dates and times of our different time stamps
    public Date[] timeStamps = new Date[6];
    private Button[] buttons ;
    private Button saveButton;
    private Context context;

    // Constructor (insert buttons which you want to set)
    public TimeStampManager(Context context, Button[] buttons, Button saveButton) {
        this.context = context;
        this.buttons = buttons;
        this.saveButton = saveButton;
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
        if (isTimeStampChecked(4)) {
            // Show popup dialog
            saveButton.setEnabled(true);
        }
    }

    // Checks if a time stamp has been filled in
    public Boolean isTimeStampChecked(int index) {
        return timeStamps[index] != null;
    }

    public Boolean timeStampsFilled() {
        int count = 0;
        for (Date stamp : this.timeStamps) {
            if (stamp!=null) {
                count+=1;
            }
        }
        return count==this.timeStamps.length;
    }

    // Resets all timeStamps and updates UI
    public void reset() {
        this.timeStamps = new Date[6];
    }

    // Summarises the results of the timeStamps into a string to be printed
    public String toString() {
        return "\nPå väg mot patient: "+tsF(timeStamps[0])+"\nAnkomst hämtplats: "+tsF(timeStamps[1])+"\nAnkomst patient: "+tsF(timeStamps[2])+"\nAvfärd hämtplats: "+tsF(timeStamps[3])+"\nAnkomst destination: "+tsF(timeStamps[4])+"\nÖverlämning: "+tsF(timeStamps[5]);
    }

    // TimeStamp format (solves null errors)
    private String tsF(Date timeStamp) {
        if (timeStamp==null) {
            return "--:--:--";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            return sdf.format(timeStamp);
        }
    }
    // Saves the dates to a file and resets the UI
    public void save() {
        // TODO not implemented yet
    }
}
