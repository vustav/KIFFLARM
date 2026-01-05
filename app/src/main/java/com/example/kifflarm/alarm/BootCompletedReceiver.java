package com.example.kifflarm.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.kifflarm.KIFFLARM;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {
            // Set your alarm here


        }
    }
}
