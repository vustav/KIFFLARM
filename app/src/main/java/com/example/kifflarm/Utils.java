package com.example.kifflarm;

import java.util.ArrayList;

public class Utils {

    public static void sortTimes(ArrayList<Alarm> alarms){

    }

    public static int insertInArraySorted(ArrayList<Alarm> alarms, Alarm newAlarm){
        for(int i = 0; i < alarms.size(); i++){
            Alarm oldAlarm = alarms.get(i);

            /*
            if (newAlarm.getHour() <= oldAlarm.getHour()) {


                if(newAlarm.getHour() == oldAlarm.getHour()) {
                    if (newAlarm.getMinute() <= oldAlarm.getMinute()) {
                        alarms.add(i, newAlarm);
                        return i;
                    }
                }
                else{
                    alarms.add(i, newAlarm);
                    return i;
                }
            }

             */

            //if hour is smaller, insert
            if(newAlarm.getHour() < oldAlarm.getHour()){
                alarms.add(i, newAlarm);
                return i;
            }

            //if same, insert if minute is smaller
            else if(newAlarm.getHour() == oldAlarm.getHour()) {
                if (newAlarm.getMinute() <= oldAlarm.getMinute()) {
                    alarms.add(i, newAlarm);
                    return i;
                }
            }
        }

        //if not already added, add it las
        alarms.add(newAlarm);
        return alarms.size()-1;
    }
}
