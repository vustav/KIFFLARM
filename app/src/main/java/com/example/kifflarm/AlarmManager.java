package com.example.kifflarm;

import com.example.kifflarm.popups.AlarmPopup;

import java.util.ArrayList;

public class AlarmManager {
    private KIFFLARM kifflarm;
    private ArrayList<Alarm> alarms;

    public AlarmManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        alarms = new ArrayList<>();

        //testing
        addAlarm(10, 45);
        addAlarm(14, 23);
        addAlarm(18, 11);
        addAlarm(21, 56);
    }

    public Alarm addAlarm(){
        Alarm alarm = new Alarm();
        alarms.add(alarm);
        return alarm;
    }

    private void addAlarm(int hour, int minute){
        Alarm alarm = new Alarm(hour, minute);
        alarms.add(alarm);
    }

    public void removeAlarm(int index){
        alarms.remove(index);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, int index, boolean newAlarm){
        openAlarmDialog(alarmsAdapter, alarms.get(index), newAlarm);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        new AlarmPopup(kifflarm, this, alarmsAdapter, alarm, newAlarm);
    }

    public void openNewAlarmDialog(AlarmsAdapter alarmsAdapter){
        openAlarmDialog(alarmsAdapter, new Alarm(), true);
    }

    public void setAlarmActive(int index, boolean on){
        //alarms.get(index).setActive(on);
    }

    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }

    public boolean getAlarmActive(int position){
        return true;
    }
}
