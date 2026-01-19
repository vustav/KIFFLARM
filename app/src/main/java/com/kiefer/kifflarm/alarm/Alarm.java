package com.kiefer.kifflarm.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.kiefer.kifflarm.FileManager;
import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.alarm.receivers.AlarmReceiver;
import com.kiefer.kifflarm.sound.Sound;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class Alarm implements Comparable<Alarm>{
    private Context context;
    private android.app.AlarmManager androidAlarmManager;
    private FileManager fileManager;
    private int hour, minute, snooze = 1;
    private boolean active;
    private int id;
    private Sound sound;

    private int color;
    public static String ALRM_INTENT_ID = "alrm_intent_id";

    /** ÄNDRA TILLBAKS OM DET INTE FUKKAR **/
    // alarmManager.setAlarmClock -> alarmManager.setExactAndAllowWhileIdle
    //ändra tillbaks i cancel

    //this is for new alarms. passing sound since alarms has to be created in AlarmActivity and can't have access to other classes (SoundManager in this case)
    public Alarm(Context context, Sound sound){
        this(context);
        this.sound = sound;
        //activate(true, true);
        active = true;
    }

    //this is for restored alarms
    //ArrayList<Alarm> alarms is because if tiles are corrupted the alarms has to be able to remove itself, just pass null if not needed
    public Alarm(Context context, ArrayList<String> params){
        this(context);
        restoreParams(params);
        //activate(active, false);
    }

    //this are shared for both and new alarms. Setting params here if restoration fails. makes the alarm useless of course =((
    private Alarm(Context context){
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

        color = Utils.getRandomColor();
    }

    public void activate(boolean active, boolean save){
        this.active = active;

        if(save) {
            saveAndSchedule();
        }
    }

    public void saveAndSchedule(){
        save();
        updateSchedule();
    }

    public void updateSchedule(){
        if(active) {
            scheduleAlarm();
        }
        else{
            cancelAlarm();
        }
    }

    /** körde new Intent i cancel förut men testar det här nu.. ändra tillbaks om det krånglar **/
    private PendingIntent pendingIntent;
    public void scheduleAlarm(){
        Log.e("Alarm ZZZ", "schedule "+hour+":"+minute);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALRM_INTENT_ID, Integer.toString(id));

        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

        pendingIntent = PendingIntent.getBroadcast(context, id, intent, flag);

        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(getTimeInMS(), pendingIntent);

        androidAlarmManager.setAlarmClock(info, pendingIntent);
    }

    //står samma intent i docs. om det krånglar, testa att anvöönda global var och köra SAMMA intent
    public void cancelAlarm(){
        Log.e("Alarm ZZZ", "cancel "+hour+":"+minute);
        /*
        androidAlarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        id, // use same id that is used to schedule the alarm to cancel it
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                )
        );

         */
        if(pendingIntent != null) {
            androidAlarmManager.cancel(pendingIntent);
            pendingIntent = null;
        }
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

    public int getSnooze() {
        return snooze;
    }

    public String getTimeAsString(){
        return getHourAsString() + ":" + getMinuteAsString();
    }

    public String getHourAsString(){
        return Utils.timeToString(getHour());
    }

    public String getMinuteAsString(){
        return Utils.timeToString(getMinute());
    }

    public int getComparableTime(){
        //returns 1156 for 11:56 for easy comparison
        return Integer.parseInt(getHourAsString()+getMinuteAsString());
    }

    public String getIdAsString(){
        return Integer.toString(id);
    }

    public int getId(){
        return id;
    }

    public String getMessage(){
        return "mememesssssaaaagggeeee";
    }

    public Sound getSound(){
        return sound;
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

    public int getColor(){
        return color;
    }

    /** SET **/
    public void setTime(int hour, int minute){
        setHour(hour);
        setMinute(minute);
    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }

    public void setSound(Sound sound){
        this.sound = sound;
        //save();
        //Log.e("Alarm ZZZ", "setSound: "+sound.getName());
    }

    /** SAVING **/
    public void save(){
        fileManager.write(getParams(), getIdAsString());
    }
    public static final String ACTIVE_TAG = "active", ALARM_ID_TAG = "alarmId", HOUR_TAG = "hour",
            MINUTE_TAG = "minute", RINGTONE_NAME_TAG = "ringtone_name", RINGTONE_URI_TAG = "ringtone_uri";

    private ArrayList<String> getParams(){
        ArrayList<String> params = new ArrayList<>();
        if(active){
            params.add(ACTIVE_TAG +"true");
        }
        else{
            params.add(ACTIVE_TAG +"false");
        }
        params.add(ALARM_ID_TAG + id);
        params.add(HOUR_TAG + hour);
        params.add(MINUTE_TAG + minute);
        params.add(RINGTONE_NAME_TAG + getSound().getName());
        params.add(RINGTONE_URI_TAG + getSound().getUri());

        return params;
    }

    private void restoreParams(ArrayList<String> params){

        //two empty strings that hopefully will be filled
        String soundName = "", soundUri = "";

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
            } else if (s.length() > RINGTONE_NAME_TAG.length() && s.substring(0, RINGTONE_NAME_TAG.length()).equals(RINGTONE_NAME_TAG)) {
                soundName = s.substring(RINGTONE_NAME_TAG.length());
            } else if (s.length() > RINGTONE_URI_TAG.length() && s.substring(0, RINGTONE_URI_TAG.length()).equals(RINGTONE_URI_TAG)) {
                soundUri = s.substring(RINGTONE_URI_TAG.length());
            }
        }

        //create the sound after loading both strings
        setSound(new Sound(soundName, Uri.parse(soundUri)));
    }

    /** COMPARE **/
    @Override
    public int compareTo(Alarm compareAlarm) {
        return getComparableTime() - compareAlarm.getComparableTime();
    }
}

