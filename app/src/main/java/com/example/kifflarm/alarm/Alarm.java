package com.example.kifflarm.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kifflarm.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class Alarm {
    private Context context;
    private android.app.AlarmManager androidAlarmManager;
    private FileManager fileManager;
    private int hour, minute;
    private boolean active;
    private int alarmId;
    public static String MESSAGE = "alarm_message";

    public Alarm(Context context){
        this.context = context;

        androidAlarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        fileManager = new FileManager(context);

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
        fileManager.write(getParams(), Integer.toString(alarmId));

        File[] files = fileManager.getFiles();
        if(files != null){
            for (File f : files) {
                Log.e("Alarm ZZZ", f.getAbsolutePath());
            }
        }
    }

    public Alarm(Context context, ArrayList<String> params){
        this.context = context;

        androidAlarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        fileManager = new FileManager(context);
        restoreParams(params);

        //when starting the app loaded alarms should already be scheduled and I guess the ID prevents
        // duplicates? We still need to re-schedule after reboots
        setActive(active);

        //if params are provided these are already saved, so don't do that
    }

    public void setActive(boolean active){
        this.active = active;
        fileManager.write(getParams(), getAlarmIdAsString());

        if(active) {
            scheduleAlarm();
        }
        else{
            cancelAlarm();
        }
    }

    //kolla upp flags
    //int flag = PendingIntent.FLAG_UPDATE_CURRENT;
    //int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
    int flag = PendingIntent.FLAG_IMMUTABLE;
    public void scheduleAlarm(){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId,
                new Intent(context, AlarmReceiver.class).putExtra(Alarm.MESSAGE, getMessage()),
                flag
        );

        androidAlarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                getTimeInMilliSec(),
                pendingIntent
        );
    }

    public void cancelAlarm(){
        androidAlarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        alarmId, // use same id that is used to schedule the alarm to cancel it
                        new Intent(context, AlarmReceiver.class),
                        flag
                )
        );
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

    public String getAlarmIdAsString(){
        return Integer.toString(alarmId);
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
        fileManager.write(getParams(), getAlarmIdAsString());
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
            Log.e("Alarm ZZZ", s);
        }
        for(String s : params){

            //check length or it will crash when  trying to get a long substring from a short string
            if(s.length() > ACTIVE_TAG.length() && s.substring(0, ACTIVE_TAG.length()).equals(ACTIVE_TAG)){
                if(s.substring(ACTIVE_TAG.length()).equals("true")){
                    active = true;
                }
                else{
                    active = false;
                }
            }

            else if(s.length() > ALARM_ID_TAG.length() && s.substring(0, ALARM_ID_TAG.length()).equals(ALARM_ID_TAG)){
                alarmId = Integer.parseInt(s.substring(ALARM_ID_TAG.length()));
            }

            else if(s.length() > HOUR_TAG.length() && s.substring(0, HOUR_TAG.length()).equals(HOUR_TAG)){
                hour = Integer.parseInt(s.substring(HOUR_TAG.length()));
            }

            else if(s.length() > MINUTE_TAG.length() && s.substring(0, MINUTE_TAG.length()).equals(MINUTE_TAG)){
                minute = Integer.parseInt(s.substring(MINUTE_TAG.length()));
            }
        }
    }
}
