package com.example.ambulansautomatisering;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeStampManager implements Parcelable {
    public Date[] timeStamps = new Date[6];
    private Date startDate;
    private Date endDate;
    private Button[] buttons;
    private Button saveButton;
    private Context context;

    // Constructor (insert buttons which you want to set)
    public TimeStampManager(Context context, Button[] buttons, Button saveButton) {
        this.startDate = new Date();
        this.context = context;
        this.buttons = buttons;
        this.saveButton = saveButton;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(timeStamps);
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
    }

    public static final Parcelable.Creator<TimeStampManager> CREATOR = new Parcelable.Creator<TimeStampManager>() {
        @Override
        public TimeStampManager createFromParcel(Parcel in) {
            return new TimeStampManager(in);
        }

        @Override
        public TimeStampManager[] newArray(int size) {
            return new TimeStampManager[size];
        }
    };

    // Constructor that reads from Parcel
    private TimeStampManager(Parcel in) {
        in.readArray(Date.class.getClassLoader());
        long startDateMillis = in.readLong();
        startDate = startDateMillis != -1 ? new Date(startDateMillis) : null;
        long endDateMillis = in.readLong();
        endDate = endDateMillis != -1 ? new Date(endDateMillis) : null;
    }

    // Sets the most recent undefined timeStamp text to current time
    public void setTime(Date timeToSet) {
        for (int i = 0; i < timeStamps.length; i++) {
            Date date = timeStamps[i];
            if (date == null) {
                timeStamps[i] = timeToSet;
                break;
            }
        }
        updateUI();
    }

    // Sets a specific timeStamp to a time
    public void setTime(int timeStampIndex, Date timeToSet) {
        timeStamps[timeStampIndex] = timeToSet;
        updateUI();
    }

    // Sets the end value of the timestampmangare
    public void complete(Date finalDate) {
        endDate = finalDate;
    }

    // Updates the UI so text corresponds to our timeStamps
    public void updateUI() {
        for (int i = 0; i < timeStamps.length; i++) {
            Date date = timeStamps[i];
            if (date != null) {
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
            if (stamp != null) {
                count += 1;
            }
        }
        return count == this.timeStamps.length;
    }

    // Summarizes the results of the timeStamps into a string to be printed
    public String toString() {
        return "På väg mot patient: " + tsF(timeStamps[0]) + "\nAnkomst hämtplats: " + tsF(timeStamps[1]) + "\nAnkomst patient: " + tsF(timeStamps[2]) + "\nAvfärd hämtplats: " + tsF(timeStamps[3]) + "\nAnkomst destination: " + tsF(timeStamps[4]) + "\nÖverlämning: " + tsF(timeStamps[5]);
    }

    // Even more brief summary of the timestamps
    public String toTitleString() {
        return "Start: " + tsF(this.startDate) + " Slut: " + tsF(this.endDate);
    }

    // TimeStamp format (solves null errors)
    private String tsF(Date timeStamp) {
        if (timeStamp == null) {
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
