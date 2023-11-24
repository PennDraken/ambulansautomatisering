package com.example.ambulansautomatisering;

import java.util.Date;


public class Task {
    double tasknr;
    
   /* Array with time stamps 1-6*/
    public Date[] time = new Date[6];
    /* Array with booleans for each time stamp, the boolean represent if we have passed the previous time stamp or not.
    E.g if we want to check for time stamp 4, then time stamp 1-3 have to be true */

   public Boolean[] time_checked = new Boolean[6];

    public Task(double tasknr) {
        this.tasknr = tasknr;
    }

    public Date getTime_stamp(Date[] time, int n) {
        return time[n-1];
    }

    public Date setTime_stamp(Date time, int n) {
        return this.time[n-1] = time;
    }

    public Boolean getTime_checked(Boolean[] time, int n) {
        return time_checked[n-1];
    }

    public Boolean setTime_checked(Boolean checked, int n) {
        return this.time_checked[n-1] = checked;
    }
}
