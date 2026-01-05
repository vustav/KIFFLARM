package com.example.kifflarm.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //String alarmMessage = intent != null ? intent.getStringExtra(Alarm.MESSAGE) : null;

        Toast t = Toast.makeText(context, "ALARMALARMALARMALARMALARMALARMALARM", Toast.LENGTH_SHORT);
        t.show();
    }
}
