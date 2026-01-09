package com.example.kifflarm.alarm;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;

import java.util.ArrayList;
import java.util.Random;

public class AlarmActivity extends AppCompatActivity {
    private Alarm alarm;
    private Vibrator vibrator;
    private ValueAnimator tvBgAnimation, tvTxtAnimation;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent();
        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        setupMediaPlayer();

        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);
        TextView bgTVtv = layout.findViewById(R.id.alarmActivityBgTv);
        Utils.setupBg(layout, bgTVtv);
        //setupBg();

        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        startVibrating();

        TextView timeTv = layout.findViewById(R.id.alarmActivityTimeTV);
        timeTv.setText(alarm.getTimeAsString());
        animateTV(timeTv);

        Button offBtn = layout.findViewById(R.id.alarmActivityOffBtn);
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmOff();
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //Log.e("AlarmActivity ZZZ", "onResume");
        playRingtone();
    }

    private void setupMediaPlayer(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            mediaPlayer.setDataSource(getApplicationContext(), alarm.getSound().getUri());
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "setupMediaPlayer"+e);
        }
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

    private void alarmOff(){
        alarm.setActive(false);
        stopVibrating();
        stopRingtone();
    }

    private Alarm getAlarm(String id){
        Alarm alarm = null;
        FileManager fileManager = new FileManager(this.getApplicationContext());
        for(ArrayList<String> params : fileManager.getParamsArray()){
            for(String s : params){
                if (s.length() > Alarm.ALARM_ID_TAG.length() && s.substring(0, Alarm.ALARM_ID_TAG.length()).equals(Alarm.ALARM_ID_TAG)) {
                    if(s.substring(Alarm.ALARM_ID_TAG.length()).equals(id)){
                        alarm = new Alarm(this.getApplicationContext(), params);
                        alarm.cancelAlarm(); //the alarm object is just to get data, so make sure it's not scheduled
                    }
                }
            }
        }
        return alarm;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        tvTxtAnimation.end();
        tvTxtAnimation = null;
        tvBgAnimation.end();
        tvBgAnimation = null;

        try {
            mediaPlayer.stop(); //throws IllegalStateException since it's probably already stopped
            mediaPlayer.release();
        }catch (IllegalStateException ese){
            ese.printStackTrace();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "onDestroy, "+e);
        }
    }
}
