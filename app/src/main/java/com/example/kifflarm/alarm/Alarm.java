package com.example.kifflarm.alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class Alarm {
    public static String MESSAGE = "alarm_message";
    private final AlarmManager alarmManager;
    private int hour, minute;
    private boolean active;

    private int alarmId;

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

        alarmId = (int) date.getTime();

        setActive(false);

        //if no params are provided this is a new alarm, save it
        alarmManager.getSaver().write(getParams());
    }

    public Alarm(AlarmManager alarmManager, ArrayList<String> params){
        this.alarmManager = alarmManager;
        restoreParams(params);

        //still active????
        //setActive(active);

        //if params are provided these are already saved, so don't do that
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

    public int getAlarmId(){
        return alarmId;
    }

    public String getMessage(){
        return "";
    }

    public long getTimeInMilliSec(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

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

    /** SAVING **/
    private final String ACTIVE_TAG = "active", ALARM_ID_TAG = "alarmId", HOUR_TAG = "hour", MINUTE_TAG = "minute";

    private ArrayList<String> getParams(){
        ArrayList<String> params = new ArrayList<>();
        if(active){
            params.add(ACTIVE_TAG +"true");
        }
        else{
            params.add(ACTIVE_TAG +"false");
        }
        params.add(ALARM_ID_TAG +alarmId);
        params.add(HOUR_TAG +hour);
        params.add(MINUTE_TAG +minute);

        return params;
    }

    private void restoreParams(ArrayList<String> params){
        for(String s : params){

            if(s.substring(0, ACTIVE_TAG.length()).equals(ACTIVE_TAG)){
                if(s.substring(ACTIVE_TAG.length()).equals("true")){
                    active = true;
                }
                else{
                    active = false;
                }
            }

            else if(s.substring(0, ALARM_ID_TAG.length()).equals(ALARM_ID_TAG)){
                alarmId = Integer.parseInt(s.substring(ALARM_ID_TAG.length()));
            }

            else if(s.substring(0, HOUR_TAG.length()).equals(HOUR_TAG)){
                hour = Integer.parseInt(s.substring(HOUR_TAG.length()));
            }

            else if(s.substring(0, MINUTE_TAG.length()).equals(MINUTE_TAG)){
                minute = Integer.parseInt(s.substring(MINUTE_TAG.length()));
            }
        }
    }
}
