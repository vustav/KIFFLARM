package com.kiefer.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kiefer.kifflarm.alarm.AlarmCannonActivity;
import com.kiefer.kifflarm.alarm.AlarmCannonNotification;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new AlarmCannonNotification(context, intent);
        //new AlarmCannonActivity(context, intent);
    }
}
