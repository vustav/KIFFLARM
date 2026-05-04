package com.kiefer.kifflarm.alarm;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;
import com.kiefer.kifflarm.utils.Utils;

import java.util.ArrayList;

public class AlarmManager {
    private KIFFLARM kifflarm;
    private String folder;
private ArrayList<Alarm> alarms;

    public AlarmManager(KIFFLARM kifflarm, String folder){
        this.kifflarm = kifflarm;
        alarms = new ArrayList<>();
        this.folder = folder;
    }
    public void loadAlarms(FileManager fileManager){
        ArrayList<ArrayList<Param>> paramsArray = fileManager.getParamsArrayFromFolder(folder);

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

    public String getFolder(){
        return folder;
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
