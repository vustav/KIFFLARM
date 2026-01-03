package com.example.kifflarm;

import java.util.Random;

public class Alarm {
    private int hour, minute;

    public Alarm(){
        Random r = new Random();
        hour = r.nextInt(23);
        minute = r.nextInt(59);
    }

    public Alarm(int hour, int minute){
        Random r = new Random();
        this.hour = hour;
        this.minute = minute;
    }

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

    public void setTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }
}
