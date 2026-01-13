package com.example.kifflarm.alarm.receiver;

import static androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;
import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmActivity;
import com.example.kifflarm.alarm.KIFFMediaPlayer;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Alarm alarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AlarmReceiver ZZZ", "onReceive");

        this.context = context;

        Intent activityIntent = new Intent(context, AlarmActivity.class);

        activityIntent.putExtra(Alarm.ALRM_INTENT_ID, intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        String channelId = alarm.getIdAsString() + "c";
        int notificationId = alarm.getId();

        mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());
        vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));

        startVibrating();
        playRingtone();

        createNotificationChannel(channelId, alarm.getTimeAsString());

        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

        //Intent fullScreenIntent = new Intent(context, AlarmActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, alarm.getId(),
                activityIntent, flag);

        Intent snoozeIntent = new Intent(context, StopReceiver.class);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(alarm.getTimeAsString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(fullScreenPendingIntent);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                        int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());

        /*


        try {
            mediaPlayer.stop(); //throws IllegalStateException since it's probably already stopped
            mediaPlayer.release();
        }catch (IllegalStateException ese){
            ese.printStackTrace();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "onDestroy, "+e);
        }
         */
    }

    private void createNotificationChannel(String id, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    id,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true); // Turn on notification light
            notificationChannel.setLightColor(Utils.getRandomColor());
            notificationChannel.enableVibration(true); // Allow vibration for notifications

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private Alarm getAlarm(String id){
        Alarm alarm = null;
        FileManager fileManager = new FileManager(context.getApplicationContext());
        for(ArrayList<String> params : fileManager.getParamsArray()){
            for(String s : params){
                if (s.length() > Alarm.ALARM_ID_TAG.length() && s.substring(0, Alarm.ALARM_ID_TAG.length()).equals(Alarm.ALARM_ID_TAG)) {
                    if(s.substring(Alarm.ALARM_ID_TAG.length()).equals(id)){
                        alarm = new Alarm(context.getApplicationContext(), params);
                        alarm.cancelAlarm(); //the alarm object is just to get data, so make sure it's not scheduled
                    }
                }
            }
        }
        return alarm;
    }



    private void playRingtone(){
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "playRingTone, " + e);
        }
    }

    private void stopRingtone(){
        mediaPlayer.stop();
    }

    private void startVibrating() {
        //exempel: https://developer.android.com/develop/ui/views/haptics/custom-haptic-effects#java_1

        if (Build.VERSION.SDK_INT >= 26) {
            long[] timings = new long[] { 50, 50, 100, 50, 50 };
            int[] amplitudes = new int[] { 64, 128, 255, 128, 64 };
            int repeat = 1; // Repeat from the second entry, index = 1.
            VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
            // repeatingEffect can be used in multiple places.

            vibrator.vibrate(repeatingEffect);
        } else {
            //deprecated in API 26
            long[] pattern = {500};
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopVibrating(){
        vibrator.cancel();
    }
}
