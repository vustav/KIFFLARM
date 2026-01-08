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

    private MediaPlayer speakersMediaPlayer;

    //speakersMediaPlayer already plays on both speakers and earphones...
    //private MediaPlayer earphonesMediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent();
        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));
        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        setupMediaPlayer();
        setupBg();

        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        startVibrating();

        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);

        TextView tv = layout.findViewById(R.id.alarmActivityTV);
        tv.setText(alarm.getTimeAsString());
        animateTV(tv);

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
        Log.e("AlarmActivity ZZZ", "onResume");
        playRingtone();
    }

    private void setupMediaPlayer(){
        try {
            speakersMediaPlayer = new MediaPlayer();
            speakersMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            speakersMediaPlayer.setDataSource(getApplicationContext(), alarm.getSound().getUri());
            speakersMediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "setupMediaPlayer"+e);
        }

/*
        if(alarm.playInPhones()) {
            try {
                earphonesMediaPlayer = new MediaPlayer();
                earphonesMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                earphonesMediaPlayer.setDataSource(getApplicationContext(), alarm.getSound().getUri());
                earphonesMediaPlayer.setLooping(true);
            } catch (Exception e) {
                //
            }
        }

 */
    }

    private void playRingtone(){
        try {
            speakersMediaPlayer.prepare();
            speakersMediaPlayer.start();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "playRingTone, " + e);
        }

        /*
        if(alarm.playInPhones()) {
            try {
                earphonesMediaPlayer.prepare();
                earphonesMediaPlayer.start();
            } catch (Exception e) {
                Log.e("AlarmActivity ZZZ", "playRingTone, " + e);
            }
        }

         */
    }

    private void stopRingtone(){
        speakersMediaPlayer.stop();

        /*
        if(alarm.playInPhones()) {
            try {
                earphonesMediaPlayer.stop();
            }catch (Exception e){
                Log.e("AlarmActivity ZZZ", "stopRingtone, "+e);
            }
        }

         */
    }

    private void setupBg(){
        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);
        layout.setBackground(Utils.getRandomGradientDrawable());

        TextView tv = layout.findViewById(R.id.alarmActivityBgTv1);

        String label = "ALARM";
        String concatLabel = "";

        int nOfCopys = 37;
        for(int copy = 0; copy <= nOfCopys; copy++){

            int start = 0;

            if(copy == 0){
                Random r = new Random();
                start = r.nextInt(label.length());
            }
            for(int i = start; i < label.length(); i++){
                concatLabel += String.valueOf(label.charAt(i));
            }
        }

        SpannableString coloredLabel = new SpannableString(concatLabel);
        for(int i = 0; i < coloredLabel.length() - 1; i++){
            coloredLabel.setSpan(new ForegroundColorSpan(Utils.getRandomColor()), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(coloredLabel);
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
            speakersMediaPlayer.stop(); //throws IllegalStateException since it's probably already stopped
            speakersMediaPlayer.release();
        }catch (IllegalStateException ese){
            ese.printStackTrace();
        } catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "onDestroy, "+e);
        }

        /*
        if(alarm.playInPhones()) {
            try {
                earphonesMediaPlayer.stop();
                earphonesMediaPlayer.release();
            } catch (Exception e) {
                Log.e("AlarmActivity ZZZ", "onDestroy, "+e);
            }
        }

         */
    }
}
