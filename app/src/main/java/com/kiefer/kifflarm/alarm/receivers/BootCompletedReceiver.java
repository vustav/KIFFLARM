package com.kiefer.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.files.Param;

import java.util.ArrayList;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {

            FileManager fileManager = new FileManager(context);

            //ArrayList<ArrayList<Param>> paramsArray = fileManager.getParamsArrayFromFolder(context.getResources().getString(R.string.custom_alarms_folder));
            ArrayList<ArrayList<Param>> paramsArray = fileManager.getAllParamsArrays();

            if(!paramsArray.isEmpty()){
                for(ArrayList<Param> params : paramsArray){
                    Alarm alarm = new Alarm(context, params);
                    alarm.updateSchedule();
                }
            }
        }
    }
}
