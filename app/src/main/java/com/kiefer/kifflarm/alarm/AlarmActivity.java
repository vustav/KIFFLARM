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
import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

public class AlarmActivity extends AppCompatActivity {
    //private Alarm alarm;
    //private Vibrator vibrator;
    private ValueAnimator tvBgAnimation, tvTxtAnimation;
    //private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("AlarmActivity ZZZ", "onCreate");
        super.onCreate(savedInstanceState);
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
                    kill(alarm, vibrator, mediaPlayer);
                }
            });

            Button snoozeBtn = layout.findViewById(R.id.alarmActivitySnoozeBtn);
            snoozeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Alarm snoozeAlarm = new Alarm(AlarmActivity.this, alarm.getSound());
                    snoozeAlarm.setTime(alarm.getHour(), alarm.getMinute() + alarm.getSnooze());
                    snoozeAlarm.activate(true, false);
                    snoozeAlarm.updateSchedule();

                    kill(alarm, vibrator, mediaPlayer);
                }
            });

            //AlarmUtils.startVibrating(vibrator);
            //AlarmUtils.playRingtone(mediaPlayer);
        }
        catch (Exception e){
            Log.e("AlarmActivity ZZZ", e.toString());
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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

    private void kill(Alarm alarm, Vibrator vibrator, MediaPlayer mediaPlayer){
        AlarmUtils.alarmOff(alarm, vibrator, mediaPlayer);
        KIFFMediaPlayer.destroy();
        KIFFVibrator.destroy();
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

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
}
