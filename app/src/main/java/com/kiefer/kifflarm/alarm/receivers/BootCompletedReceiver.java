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
        Log.e("BootCompletedReceiver ZZZ", "1");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {
            Log.e("BootCompletedReceiver ZZZ", "2");
            // Set your alarm here
            FileManager fileManager = new FileManager(context);

            ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();

            if(!paramsArray.isEmpty()){
                for(ArrayList<String> params : paramsArray){
                    Alarm alarm = new Alarm(context, params);
                    Log.e("BootCompletedReceiver ZZZ", "alarma active: "+alarm.isActive());
                    alarm.updateSchedule();
                }
            }
        }
    }
}
