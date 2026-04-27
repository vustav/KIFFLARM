package com.kiefer.kifflarm.alarm;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.files.Param;
import com.kiefer.kifflarm.utils.Utils;

import java.util.ArrayList;

public class AlarmManager {
    private KIFFLARM kifflarm;
private ArrayList<Alarm> alarms;

    public AlarmManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
    }
    public void loadAlarms(ArrayList<ArrayList<Param>> paramsArray){
        alarms = new ArrayList<>();

        //recreate saved alarms if there are any
        //ArrayList<ArrayList<Param>> paramsArray = kifflarm.getFileManager().getParamsArray();
        if(!paramsArray.isEmpty()){
            for(ArrayList<Param> params : paramsArray){
                alarms.add(new Alarm(kifflarm, params));
            }
        }

        Utils.sortAlarms(alarms);
    }

    /** GET **/
    public ArrayList<Alarm> getAlarms(){
        return alarms;
    }
    public void sortAlarms(){
        Utils.sortAlarms(getAlarms());
    }

    public int getAlarmsSize(){
        return alarms.size();
    }

    public Alarm getAlarm(int index){
        return alarms.get(index);
    }

    public void removeAlarm(int index){
        alarms.remove(index).removeAlarm();
    }
}
