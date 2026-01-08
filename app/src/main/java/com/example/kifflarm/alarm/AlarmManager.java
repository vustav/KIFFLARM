package com.example.kifflarm.alarm;

import android.util.Log;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.popups.AlarmPopup;

import java.util.ArrayList;

public class AlarmManager {
    private KIFFLARM kifflarm;

    private FileManager fileManager;
    private ArrayList<Alarm> alarms;

    public AlarmManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        fileManager = new FileManager(kifflarm);

        alarms = new ArrayList<>();

        //recreate saved alarms if there are any
        ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();

        if(!paramsArray.isEmpty()){
            for(ArrayList<String> params : paramsArray){
                //try {
                    alarms.add(new Alarm(kifflarm, params));
                //}
                //catch (Exception e){
                    //Log.e("AlarmManager ZZZ", "cottupted params? "+e);
                //}
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
        new AlarmPopup(kifflarm, this, alarmsAdapter, alarm, newAlarm);
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
