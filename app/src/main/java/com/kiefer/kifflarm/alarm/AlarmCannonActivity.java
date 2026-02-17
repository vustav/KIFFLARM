package com.kiefer.kifflarm.alarm;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;

import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

public class AlarmCannonActivity {
    private Context context;

    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    public AlarmCannonActivity(Context context, Intent intent){
        Log.e("AlarmCannonActivity ZZZ", "yes");
        this.context = context;

        alarm = FileManager.getAlarm(context, intent.getStringExtra(Alarm.ALRM_ID_TAG));

        mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());
        vibrator = KIFFVibrator.getInstance(context);

        AlarmUtils.startVibrating(vibrator);
        AlarmUtils.playRingtone(mediaPlayer);

        Intent activityIntent = new Intent(context, AlarmActivity.class);
        activityIntent.putExtra(Alarm.ALRM_ID_TAG, intent.getStringExtra(Alarm.ALRM_ID_TAG));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityOptions.setPendingIntentCreatorBackgroundActivityStartMode (ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED);
        }
        Bundle bundle = activityOptions.toBundle();

        context.startActivity(activityIntent, bundle);

        //a timer that turns off the alarm after a set time
        int duration = 50000; //50 secs
        new CountDownTimer(duration, duration) {
            public void onTick(long millisUntilFinished) {
                //
            }
            public void onFinish() {
                //NotificationManagerCompat.from(context).cancel(notificationId);
                AlarmUtils.alarmOff(alarm, vibrator, mediaPlayer);

                try {
                    //Activity will be started before notification is clicked if the phone was sleeping (no idea why)
                    if (AlarmActivity.isActive) {
                        AlarmActivity.killActivity();
                    }
                }
                catch (Exception e){
                    Log.e("AlarmCannon ZZZ", "timer");
                }
            }
        }.start();
    }
}
