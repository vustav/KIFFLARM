package com.kiefer.kifflarm.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;
import com.kiefer.kifflarm.alarm.receivers.NotificationDismissedReceiver;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;

//unnecessary class only used to test alarms
public class AlarmCannonNotification {
    private Context context;
    private static CountDownTimer countDownTimer;
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private float rampVolume = 0; //used to ramp volume during alarm
    public static String NOTIFICATION_ID_TAG = "nidt";

    public AlarmCannonNotification(Context context, Intent intent){
        this.context = context;

        alarm = FileManager.getAlarm(context, intent.getStringExtra(Alarm.ALRM_ID_TAG));
        KIFFLARM.setOngoingAlarm(alarm); //this is for the main app to know if an alarm is running when it starts

        String channelId = alarm.getIdAsString() + "c";
        int notificationId = alarm.getId();

        mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());
        vibrator = KIFFVibrator.getInstance(context);

        AlarmUtils.startVibrating(vibrator);
        AlarmUtils.playRingtone(mediaPlayer);

        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

        createNotificationChannel(channelId, alarm.getTimeAsString());

        //activity
        Intent activityIntent = new Intent(context, AlarmActivity.class);
        activityIntent.putExtra(Alarm.ALRM_ID_TAG, intent.getStringExtra(Alarm.ALRM_ID_TAG));
        activityIntent.putExtra(NOTIFICATION_ID_TAG, Integer.toString(notificationId));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, notificationId, activityIntent, flag);

        //swipe
        Intent swipeIntent = new Intent(context, NotificationDismissedReceiver.class);
        swipeIntent.putExtra(Alarm.ALRM_ID_TAG, intent.getStringExtra(Alarm.ALRM_ID_TAG));
        PendingIntent swipePendingIntent = PendingIntent.getBroadcast(context, notificationId, swipeIntent, flag);

        RemoteViews remoteViews = getRemoteViews(alarm);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.custom_btn)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(alarm.getTimeAsString())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setDeleteIntent(swipePendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(fullScreenPendingIntent)
                .setCustomContentView(remoteViews);

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

        /** RAMP WORKS BAD AND NEEDS FIXING **/
        //a timer that turns off the alarm after a set time
        int duration = 50000; //50 secs
        int rampTime = 10000;
        int tick = 1000;
        float multiplier = 0;

        //create a nice ramping volume
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //start by saving current volume
        int startVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        Log.e("Cannon ZZZ", "startVol: "+startVolume);

        //lower the volume to start the ramp
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
        //set rampStart to whatever the system allows as it's lowest
        float rampStart = (float)audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        rampVolume = rampStart;
        Log.e("Cannon ZZZ", "rampBottpm: "+rampStart);
        countDownTimer = new CountDownTimer(duration, tick) {
            public void onTick(long millisUntilFinished) {
                if(duration - millisUntilFinished < rampTime){
                    //update rampVolume every tick. We need this as a float and cast it to int when setting,
                    //otherwise small increments will not register, ex. 1 -> 1.2 will be 1 -> 1 and
                    //without rampVolume 1 would be used next tick and the same thing would happen again and the volume would be stuck at 1
                    rampVolume += ((float) startVolume - rampStart) / ((float) rampTime/(float) tick);
                    //Log.e("Cannon ZZZ", "ramp: " + rampVolume);
                    if(rampVolume <= startVolume) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int)rampVolume, 0);
                        Log.e("Cannon ZZZ", "getVol: " + audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
                    }
                }
                else{
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, startVolume, 0);
                    Log.e("Cannon ZZZ", "getVol: " + audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
                }
            }
            public void onFinish() {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, startVolume, 0); //should already be back to start but just in case

                NotificationManagerCompat.from(context).cancel(notificationId);
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

    private RemoteViews getRemoteViews(Alarm alarm){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);

        remoteViews.setTextViewText(R.id.notificationTV, alarm.getTimeAsString());
        remoteViews.setTextColor(R.id.notificationTV, Utils.getRandomColor());

        remoteViews.setTextViewText(R.id.notificationTVShadow, alarm.getTimeAsString());

        return remoteViews;
    }

    public static void stopTimer(){
        if(countDownTimer != null) {
            countDownTimer.onFinish();
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
