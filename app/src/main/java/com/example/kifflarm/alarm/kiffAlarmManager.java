package com.example.kifflarm.alarm;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.Utils;
import com.example.kifflarm.popups.AlarmSettingsPopup;

import java.util.ArrayList;

public class kiffAlarmManager {
    private KIFFLARM kifflarm;

    private FileManager fileManager;
    private ArrayList<Alarm> alarms;

    public kiffAlarmManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        fileManager = new FileManager(kifflarm);

        loadAlarms();

        Utils.sortAlarms(alarms);
    }

    public void loadAlarms(){
        alarms = new ArrayList<>();

        //recreate saved alarms if there are any
        ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();

        if(!paramsArray.isEmpty()){
            for(ArrayList<String> params : paramsArray){
                alarms.add(new Alarm(kifflarm, params));
            }
        }
    }

    public void removeAlarm(int index){
        alarms.remove(index).removeAlarm();
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, int index, boolean newAlarm){
        openAlarmDialog(alarmsAdapter, alarms.get(index), newAlarm);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        new AlarmSettingsPopup(kifflarm, this, alarmsAdapter, alarm, newAlarm);
    }

    public void openNewAlarmDialog(AlarmsAdapter alarmsAdapter){
        openAlarmDialog(alarmsAdapter, new Alarm(kifflarm, kifflarm.getSoundManager().getRandomSound()), true);
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
