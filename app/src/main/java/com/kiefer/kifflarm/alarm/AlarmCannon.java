package com.kiefer.kifflarm.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kiefer.kifflarm.FileManager;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;
import com.kiefer.kifflarm.alarm.receivers.NotificationDismissedReceiver;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;

//unnecessary class only used to test alarms
public class AlarmCannon {
    private Context context;

    public AlarmCannon(Context context, Intent intent){
        this.context = context;

        Log.e("AlarmCannon ZZZ", "intent == null"+(intent == null));
        Log.e("AlarmCannon ZZZ", "id: "+intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        Alarm alarm = FileManager.getAlarm(context, intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        Log.e("AlarmCannon ZZZ", "alarm == null"+(alarm == null));

        String channelId = alarm.getIdAsString() + "c";
        int notificationId = alarm.getId();

        MediaPlayer mediaPlayer = KIFFMediaPlayer.getInstance(context, alarm.getSound().getUri());
        Vibrator vibrator = KIFFVibrator.getInstance(context);

        AlarmUtils.startVibrating(vibrator);
        AlarmUtils.playRingtone(mediaPlayer);

        int flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;

        createNotificationChannel(channelId, alarm.getTimeAsString());

        //activity
        Intent activityIntent = new Intent(context, AlarmActivity.class);
        activityIntent.putExtra(Alarm.ALRM_INTENT_ID, intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, notificationId, activityIntent, flag);

        //swipe
        Intent swipeIntent = new Intent(context, NotificationDismissedReceiver.class);
        swipeIntent.putExtra(Alarm.ALRM_INTENT_ID, intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        PendingIntent swipePendingIntent = PendingIntent.getBroadcast(context, notificationId, swipeIntent, flag);

        //style
        //NotificationCompat.DecoratedCustomViewStyle style = new NotificationCompat.DecoratedCustomViewStyle();

        RemoteViews remoteViews = getRemoteViews(alarm);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.custom_btn)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(alarm.getTimeAsString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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

        /*
        RelativeLayout layout = new RelativeLayout(context);
        //RelativeLayout layout1 = remoteViews.
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(100, 100);
        layout.setLayoutParams(rlp);
        TextView tv = new TextView(context);
        layout.addView(tv);
        Utils.createNiceBg(layout, tv, 20);

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        layout.draw(canvas);

         */

        //remoteViews.setImageViewBitmap(R.id.notificationIV, bitmap);
        remoteViews.setTextViewText(R.id.notificationTV, alarm.getTimeAsString());
        remoteViews.setTextColor(R.id.notificationTV, Utils.getRandomColor());
        //remoteViews.setTextViewTextSize(R.id.notificationTV, TypedValue.COMPLEX_UNIT_SP, 30);

        remoteViews.setTextViewText(R.id.notificationTVShadow, alarm.getTimeAsString());
        //remoteViews.setTextViewTextSize(R.id.notificationTV, TypedValue.COMPLEX_UNIT_SP, 30);

        return remoteViews;
    }
}
