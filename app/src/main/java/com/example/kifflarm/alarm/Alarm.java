package com.example.kifflarm.alarm;

import android.util.Log;
import android.widget.Toast;

import com.example.kifflarm.KIFFLARM;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Alarm {
    public static String MESSAGE = "alarm_message";
    private AlarmManager alarmManager;
    private int hour, minute;
    private boolean active;

    private int id;

    public Alarm(AlarmManager alarmManager){
        this.alarmManager = alarmManager;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        hour = date.getHours();
        minute = date.getMinutes() + 10;

        if(minute > 59){
            minute -= 60;

            hour++;

            if(hour > 23){
                hour -= 24;
            }
        }

        id = (int) date.getTime();

        setActive(false);
    }

    public Alarm(AlarmManager alarmManager, int hour, int minute){
        this(alarmManager);

        this.hour = hour;
        this.minute = minute;
    }

    public void setActive(boolean active){
        this.active = active;

        if(active) {
            alarmManager.scheduleAlarm(this);
        }
        else{
            alarmManager.cancelAlarm(this);
        }
    }

    /** GET **/
    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public String getTimeAsString(){
        return getHourAsString() + ":" + getMinuteAsString();
    }

    public String getHourAsString(){

        //add 0 to the start if below 10
        String hour = Integer.toString(getHour());
        if(getHour() < 10){
            hour = "0"+getHour();
        }
        return hour;
    }

    public String getMinuteAsString(){
        String minute = Integer.toString(getMinute());
        if(getMinute() < 10){
            minute = "0"+getMinute();
        }
        return minute;
    }

    public int getId(){
        return id;
    }

    public String getMessage(){
        return "";
    }

    public long getTimeInMilliSec(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    public boolean isActive(){
        return active;
    }

    /** SET **/
    public void setTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }
}
