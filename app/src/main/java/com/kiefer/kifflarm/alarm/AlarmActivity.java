package com.kiefer.kifflarm.alarm;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kiefer.kifflarm.FileManager;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

public class AlarmActivity extends AppCompatActivity {
    public static boolean isActive = false, kill = false;
    //private Alarm alarm;
    //private Vibrator vibrator;
    private ValueAnimator tvBgAnimation, tvTxtAnimation;
    //private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("AlarmActivity ZZZ", "onCreate");
        super.onCreate(savedInstanceState);
        isActive = true;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        try {
            Intent intent = getIntent();

            Alarm alarm = FileManager.getAlarm(this, intent.getStringExtra(Alarm.ALRM_INTENT_ID));

            MediaPlayer mediaPlayer = KIFFMediaPlayer.getInstance(this, alarm.getSound().getUri());

            RelativeLayout layout = findViewById(R.id.alarmActivityLayout);
            TextView bgTVtv = layout.findViewById(R.id.alarmActivityBgTv);
            Utils.createNiceBg(layout, bgTVtv, 65);

            Vibrator vibrator = KIFFVibrator.getInstance(this);

            TextView timeTv = layout.findViewById(R.id.alarmActivityTimeTV);
            timeTv.setText(alarm.getTimeAsString());
            animateTV(timeTv);

            Button offBtn = layout.findViewById(R.id.alarmActivityOffBtn);
            offBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    killAlarm(alarm, vibrator, mediaPlayer);
                }
            });

            Button snoozeBtn = layout.findViewById(R.id.alarmActivitySnoozeBtn);
            snoozeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm newAlarm = new Alarm(AlarmActivity.this, alarm.getSound());
                    newAlarm.setIsSnooze(true);
                    newAlarm.setTime(alarm.getHour(), alarm.getMinute() + alarm.getSnoozeTime());
                    newAlarm.activate(true);
                    newAlarm.saveAndSchedule();
                    killAlarm(alarm, vibrator, mediaPlayer);
                }
            });
        }
        catch (Exception e){
            Log.e("AlarmActivity ZZZ", e.toString());
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        /*
        This is the result of a weird behaviour where AlarmActivity starts before the notification is tapped
        if the phone is woken from sleep. To get around this NotificationDismissedListener set kill = true
        and we kill the activity here
         */
        if(kill){
            finish();
        }
    }

    private void animateTV(TextView tv){
        int tvAnimationTime = 550;
        int colorFrom = Utils.getRandomColor();
        int colorTo = Utils.getContrastColor(colorFrom);

        //bg animation
        tvBgAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        tvBgAnimation.setDuration(tvAnimationTime);
        tvBgAnimation.setRepeatCount(ValueAnimator.INFINITE);
        tvBgAnimation.setRepeatMode(ValueAnimator.REVERSE);
        tvBgAnimation.addUpdateListener(animator -> tv.setBackgroundColor((int) animator.getAnimatedValue()));
        tvBgAnimation.start();

        //txt animation
        tvTxtAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
        tvTxtAnimation.setDuration(tvAnimationTime); // milliseconds
        tvTxtAnimation.setRepeatCount(ValueAnimator.INFINITE);
        tvTxtAnimation.setRepeatMode(ValueAnimator.REVERSE);
        tvTxtAnimation.addUpdateListener(animator -> tv.setTextColor((int) animator.getAnimatedValue()));
        tvTxtAnimation.start();
    }

    private void killAlarm(Alarm alarm, Vibrator vibrator, MediaPlayer mediaPlayer){
        AlarmUtils.alarmOff(alarm, vibrator, mediaPlayer);
        KIFFMediaPlayer.destroy();
        KIFFVibrator.destroy();
        finish();

        if(alarm.isSnooze()){
            alarm.removeAlarm();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        isActive = false;

        tvTxtAnimation.end();
        tvTxtAnimation = null;
        tvBgAnimation.end();
        tvBgAnimation = null;

        try {
            KIFFMediaPlayer.destroy();
        }catch (IllegalStateException ese){
            ese.printStackTrace();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "onDestroy, "+e);
        }
    }

    public static boolean isActive(){
        //explanation in onResume
        return isActive;
    }

    public static void killActivity(){
        //explanation in onResume
        kill = true;
    }
}
