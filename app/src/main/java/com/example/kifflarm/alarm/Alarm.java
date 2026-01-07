package com.example.kifflarm.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kifflarm.FileManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class Alarm {
    private Context context;
    private android.app.AlarmManager androidAlarmManager;
    private FileManager fileManager;
    private int hour, minute;
    private boolean active;
    private int id;
    private String ringtone = "ringtoneTTTOOOOOOOOOO";
    public static String ALRM_INTENT_ID = "alrm_intent_id", ALRM_INTENT_MESSAGE = "alrm_intent__msg", ALRM_INTENT_TONE = "alrm_intent_ringtone";

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

        id = (int) date.getTime();
        //Log.e("Alarm ZZZ", "id: "+ id);

        setActive(false);
        //setTime(hour, minute);

        //if no params are provided this is a new alarm, save it (setActive calls write)
        //fileManager.write(getParams(), Integer.toString(alarmId));
    }

    //ArrayList<Alarm> alarms is because if tiles are corrupted the alarms has to be able to remove itself, just pass null if not needed
    public Alarm(Context context, ArrayList<String> params){
        this.context = context;

        androidAlarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        fileManager = new FileManager(context);
        restoreParams(params);

        setActive(active);
    }

    public void setActive(boolean active){
        this.active = active;
        fileManager.write(getParams(), getAlarmIdAsString());
        updateSchedule();
    }

    public void updateSchedule(){
        if(active) {
            //cancelAlarm();
            scheduleAlarm();
        }
        else{
            cancelAlarm();
        }
    }

    public void scheduleAlarm(){
        Log.e("Alarm ZZZ", "schedule "+hour+":"+minute);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALRM_INTENT_ID, Integer.toString(id));
        intent.putExtra(ALRM_INTENT_TONE, getRingTone());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        androidAlarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                getTimeInMS(),
                pendingIntent
        );
    }

    public void cancelAlarm(){
        Log.e("Alarm ZZZ", "cancel "+hour+":"+minute);
        androidAlarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        id, // use same id that is used to schedule the alarm to cancel it
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
    }

    public void removeAlarm(){
        cancelAlarm();
        fileManager.delete(this);
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
        return Integer.toString(id);
    }

    public int getId(){
        return id;
    }

    public String getMessage(){
        return "mememesssssaaaagggeeee";
    }

    public String getRingTone(){
        return ringtone;
    }

    public long getTimeInMS(){
        Calendar calendar = Calendar.getInstance();
        //calendar.clear();

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        //Log.e("Alarm ZZZ", "timeInMS 1: "+calendar.getTimeInMillis());

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
        updateSchedule();
    }

    public void setRingTone(String ringtone){
        this.ringtone = ringtone;
    }


    /** SAVING **/
    public static final String ACTIVE_TAG = "active", ALARM_ID_TAG = "alarmId", HOUR_TAG = "hour", MINUTE_TAG = "minute", RINGTONE_TAG = "ringtone";

    private ArrayList<String> getParams(){
        ArrayList<String> params = new ArrayList<>();
        if(active){
            params.add(ACTIVE_TAG +"true");
        }
        else{
            params.add(ACTIVE_TAG +"false");
        }
        params.add(ALARM_ID_TAG + id);
        params.add(HOUR_TAG +hour);
        params.add(MINUTE_TAG +minute);
        params.add(RINGTONE_TAG +getRingTone());

        return params;
    }

    private void restoreParams(ArrayList<String> params){
        for(String s : params){
            //check length or it will crash when  trying to get a long substring from a short string
            if (s.length() > ACTIVE_TAG.length() && s.substring(0, ACTIVE_TAG.length()).equals(ACTIVE_TAG)) {
                if (s.substring(ACTIVE_TAG.length()).equals("true")) {
                    active = true;
                } else {
                    active = false;
                }
            } else if (s.length() > ALARM_ID_TAG.length() && s.substring(0, ALARM_ID_TAG.length()).equals(ALARM_ID_TAG)) {
                id = Integer.parseInt(s.substring(ALARM_ID_TAG.length()));
            } else if (s.length() > HOUR_TAG.length() && s.substring(0, HOUR_TAG.length()).equals(HOUR_TAG)) {
                hour = Integer.parseInt(s.substring(HOUR_TAG.length()));
            } else if (s.length() > MINUTE_TAG.length() && s.substring(0, MINUTE_TAG.length()).equals(MINUTE_TAG)) {
                minute = Integer.parseInt(s.substring(MINUTE_TAG.length()));
            } else if (s.length() > RINGTONE_TAG.length() && s.substring(0, RINGTONE_TAG.length()).equals(RINGTONE_TAG)) {
                setRingTone(s.substring(RINGTONE_TAG.length()));
            }
        }
    }
}

