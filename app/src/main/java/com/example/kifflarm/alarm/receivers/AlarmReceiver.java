package com.example.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmCannon;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Alarm alarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver ZZZ", "onReceive");

        new AlarmCannon(context, intent);
    }
}
