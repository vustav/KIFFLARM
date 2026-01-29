package com.kiefer.kifflarm.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.kiefer.kifflarm.FileManager;
import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmActivity;
import com.kiefer.kifflarm.alarm.AlarmUtils;
import com.kiefer.kifflarm.alarm.TriggerOnResumeActivity;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Alarm alarm = FileManager.getAlarm(context, intent.getStringExtra(Alarm.ALRM_ID_TAG));

            MediaPlayer mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());

            Vibrator vibrator = KIFFVibrator.getInstance(context);

            AlarmUtils.alarmOff(alarm, vibrator, mediaPlayer);
            KIFFMediaPlayer.destroy();
            KIFFVibrator.destroy();

            //se explanation in TriggerOnResumeActivity
            Intent onResumeIntent = new Intent(context, TriggerOnResumeActivity.class);
            onResumeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(onResumeIntent);

            //explanation in AlarmActivity.onResume
            try {
                if (AlarmActivity.isActive()) {
                    AlarmActivity.killActivity();
                }
            }
            catch (Exception e){
                Log.e("NotificationDismissedReceiver ZZZ", "onReceive, "+e);
            }
        }
        catch (Exception e){
            Log.e("NotificationDismissedReceiver ZZZ", e.toString());
        }
    }
}
