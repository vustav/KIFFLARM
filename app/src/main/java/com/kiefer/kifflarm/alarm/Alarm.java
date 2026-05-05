package com.kiefer.kifflarm.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.alarm.receivers.AlarmReceiver;
import com.kiefer.kifflarm.sound.Sound;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class Alarm implements Comparable<Alarm>{
    private Context context;
    private android.app.AlarmManager alarmManager;
    private FileManager fileManager;
    private String folder = "";
    protected int hour, minute, snoozeTime = 5;
    protected boolean active;

    //when the user clicks snooze a new alarm with this true is created. it's used to differ between
    // a snooze and a user created alarm since snoozes are deleted after the alarm while regular alarms
    // is only deactivated.
    private boolean isSnooze;

    protected int id;
    private Sound sound;
    public static String ALRM_ID_TAG = "alrm_intent_id";

    //this is for new alarms. passing sound since alarms has to be created in AlarmActivity and can't have access to other classes (SoundManager in this case)
    public Alarm(Context context, Sound sound, String folder){
        this(context);
        this.sound = sound;
        //activate(true, true);
        active = true;
        this.folder = folder;
    }

    //this is for restored alarms
    //ArrayList<Alarm> alarms is because if tiles are corrupted the alarms has to be able to remove itself, just pass null if not needed
    public Alarm(Context context, ArrayList<Param> params){
        this(context);
        restoreParams(params);

        //this shouldn't be needed except for after some system failure
        activate(active);
    }

    //this are shared for both and new alarms. Setting params here if restoration fails. makes the alarm useless of course =((
    private Alarm(Context context){
        this.context = context;
        isSnooze = false;

        alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
    }

    public void activate(boolean active){
        this.active = active;
        saveAndSchedule();
    }

    private void saveAndSchedule(){
        save();
        updateSchedule();
    }

    public void updateSchedule(){
        //Log.e("Alarm ZZZ", "updateSchedule");
        if(active) {
            scheduleAlarm();
        }
        else{
            cancelAlarm();
        }
    }

    private void scheduleAlarm(){
        Log.e("Alarm ZZZ", "schedule "+hour+":"+minute);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ALRM_ID_TAG, Integer.toString(id));

        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, flag);
        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(getTimeInMS(), pendingIntent);
        alarmManager.setAlarmClock(info, pendingIntent);
    }

    private void cancelAlarm(){
        Log.e("Alarm ZZZ", "cancel "+hour+":"+minute);

        alarmManager.cancel(
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

    public int getSnoozeTime() {
        return snoozeTime;
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

    public String getSnoozeAsString(){
        return Utils.timeToString(getSnoozeTime());
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

    public String getFullPath(){
        return folder + "/" + getIdAsString()+"."+context.getResources().getString(R.string.alarms_extension);
    }

    public String getMessage(){
        return "mememesssssaaaagggeeee";
    }

    public Sound getSound(){
        return sound;
    }

    public String getFolder() {
        return folder;
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
        //Log.e("Alarm ZZZ", "getActive: "+active);
        return active;
    }

    public boolean isSnooze() {
        return isSnooze;
    }

    /** SET **/
    public void setTime(int hour, int minute){
        setHour(hour);
        setMinute(minute);
    }

    public void setSnoozeTime(int snooze){
        snoozeTime = snooze;
    }
    public void setHour(int hour){
        this.hour = hour;
    }
    public void setMinute(int minute){
        this.minute = minute;
    }

    public void setSound(Sound sound){
        this.sound = sound;
    }

    public void setIsSnooze(boolean snooze){
        isSnooze = snooze;
    }

    /** SAVING **/
    public void save(){
        //String path = folder + "/" + getIdAsString();
        //Log.e("Alarm ZZZ", "save, path: "+path);
        fileManager.write(getParams(), folder, getIdAsString(), context.getResources().getString(R.string.alarms_extension));
    }
    public static final String ACTIVE_TAG = "active", ALARM_ID_TAG = "alarmId", HOUR_TAG = "hour",
            MINUTE_TAG = "minute", RINGTONE_NAME_TAG = "ringtone_name", RINGTONE_URI_TAG = "ringtone_uri",
            IS_SNOOZE_TAG = "snooze_on", SNOOZE_TIME_TAG = "snooze_time", FOLDER_TAG = "folder";

    protected ArrayList<Param> getParams(){
        ArrayList<Param> params = new ArrayList<>();
        params.add(new Param(ACTIVE_TAG, Boolean.toString(active)));
        params.add(new Param(ALARM_ID_TAG, Integer.toString(id)));
        params.add(new Param(HOUR_TAG, Integer.toString(hour)));
        params.add(new Param(MINUTE_TAG, Integer.toString(minute)));
        params.add(new Param(RINGTONE_NAME_TAG, getSound().getName()));
        params.add(new Param(RINGTONE_URI_TAG, getSound().getUri().toString()));
        params.add(new Param(IS_SNOOZE_TAG, Boolean.toString(isSnooze)));
        params.add(new Param(SNOOZE_TIME_TAG, Integer.toString(snoozeTime)));
        params.add(new Param(FOLDER_TAG, folder));
        return params;
    }

    private void restoreParams(ArrayList<Param> params){

        //two empty strings that hopefully will be filled
        String soundName = "", soundUri = "";

        for (Param p : params) {
            if (p.key.equals(ACTIVE_TAG)) {
                active = Boolean.parseBoolean(p.value);
            } else if (p.key.equals(ALARM_ID_TAG)) {
                id = Integer.parseInt(p.value);
            } else if (p.key.equals(HOUR_TAG)) {
                hour = Integer.parseInt(p.value);
            } else if (p.key.equals(MINUTE_TAG)) {
                minute = Integer.parseInt(p.value);
            } else if (p.key.equals(RINGTONE_NAME_TAG)) {
                soundName = p.value;
            } else if (p.key.equals(RINGTONE_URI_TAG)) {
                soundUri = p.value;
            } else if (p.key.equals(IS_SNOOZE_TAG)) {
                isSnooze = Boolean.parseBoolean(p.value);
            } else if (p.key.equals(SNOOZE_TIME_TAG)) {
                snoozeTime = Integer.parseInt(p.value);
            } else if (p.key.equals(FOLDER_TAG)) {
                folder = p.value;
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

