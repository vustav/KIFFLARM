package com.kiefer.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new AlarmReceiver2(context, intent);
        //new AlarmCannonActivity(context, intent);
    }
}
