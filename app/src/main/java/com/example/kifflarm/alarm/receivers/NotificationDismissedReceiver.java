package com.example.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmUtils;
import com.example.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.example.kifflarm.alarm.singles.KIFFVibrator;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    //private Context context;
    //private Alarm alarm;
    //private MediaPlayer mediaPlayer;
    //private Vibrator vibrator;
    @Override
    public void onReceive(Context context, Intent intent) {
        //this.context = context;
        try {
            Alarm alarm = FileManager.getAlarm(context, intent.getStringExtra(Alarm.ALRM_INTENT_ID));

            MediaPlayer mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());

            Vibrator vibrator = KIFFVibrator.getInstance(context);

            AlarmUtils.alarmOff(alarm, vibrator, mediaPlayer);
            KIFFMediaPlayer.destroy();
            KIFFVibrator.destroy();
        }
        catch (Exception e){
            Log.e("NotificationDismissedReceiver ZZZ", e.toString());
        }
    }
}
