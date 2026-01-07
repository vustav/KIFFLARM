package com.example.kifflarm.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.KIFFLARM;

import java.util.ArrayList;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {
            // Set your alarm here
            FileManager fileManager = new FileManager(context);

            ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();

            if(!paramsArray.isEmpty()){
                for(ArrayList<String> params : paramsArray){
                    try {
                        new Alarm(context, params);
                    }
                    catch (Exception e){
                        Log.e("BootCompletedReceiver ZZZ", e.toString());
                    }
                }
            }
        }
    }
}
