package com.example.kifflarm.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.popups.AlarmPopup;

import java.util.ArrayList;

public class AlarmManager {
    private KIFFLARM kifflarm;
    private android.app.AlarmManager androidAlarmManager;
    private ArrayList<Alarm> alarms;

    public AlarmManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        alarms = new ArrayList<>();

        androidAlarmManager = (android.app.AlarmManager) kifflarm.getSystemService(Context.ALARM_SERVICE);

        //testing
        alarms.add(new Alarm(this, 10, 45));
        alarms.add(new Alarm(this, 14, 23));
        alarms.add(new Alarm(this, 18, 11));
        alarms.add(new Alarm(this, 21, 56));
    }

    //kolla upp flags
    //int flag = PendingIntent.FLAG_UPDATE_CURRENT;
    //int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
    int flag = PendingIntent.FLAG_IMMUTABLE;
    public void scheduleAlarm(Alarm alarm){

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                kifflarm,
                alarm.getId(),
                new Intent(kifflarm, AlarmReceiver.class).putExtra(Alarm.MESSAGE, alarm.getMessage()),
                flag
        );

        androidAlarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                alarm.getTimeInMilliSec(),
                pendingIntent
        );
    }

    public void cancelAlarm(Alarm alarm){
        androidAlarmManager.cancel(
                PendingIntent.getBroadcast(
                        kifflarm,
                        alarm.getId(), // use same id that is used to schedule the alarm to cancel it
                        new Intent(kifflarm, AlarmReceiver.class),
                        flag
                )
        );
    }

    public void removeAlarm(int index){
        Alarm alarm = alarms.remove(index);
        cancelAlarm(alarm);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, int index, boolean newAlarm){
        openAlarmDialog(alarmsAdapter, alarms.get(index), newAlarm);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        new AlarmPopup(kifflarm, this, alarmsAdapter, alarm, newAlarm);
    }

    public void openNewAlarmDialog(AlarmsAdapter alarmsAdapter){
        openAlarmDialog(alarmsAdapter, new Alarm(this), true);
    }

    /** SET **/
    public void setAlarmActive(int index, boolean on){
        alarms.get(index).setActive(on);
    }

    /** GET **/
    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }

    public boolean getAlarmActive(int position){
        return alarms.get(position).isActive();
    }
}
