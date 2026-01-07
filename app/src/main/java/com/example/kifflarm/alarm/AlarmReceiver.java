package com.example.kifflarm.alarm;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.getIntent;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent activityIntent = new Intent(context, AlarmActivity.class);

        //activityIntent.putExtra(Alarm.ALRM_INTENT_TONE, intent.getStringExtra(Alarm.ALRM_INTENT_TONE));
        activityIntent.putExtra(Alarm.ALRM_INTENT_ID, intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(activityIntent);
    }
}
