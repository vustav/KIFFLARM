package com.kiefer.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kiefer.kifflarm.FileManager;
import com.kiefer.kifflarm.alarm.Alarm;

import java.util.ArrayList;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {

            FileManager fileManager = new FileManager(context);

            ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();

            if(!paramsArray.isEmpty()){
                for(ArrayList<String> params : paramsArray){
                    Alarm alarm = new Alarm(context, params);
                    alarm.updateSchedule();
                }
            }
        }
    }
}
