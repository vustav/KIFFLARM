package com.example.kifflarm.alarm;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
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

    private Ringtone ringtone;
    private ValueAnimator tvBgAnimation, tvTxtAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        setupBg();

        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        startVibrating();

        Intent intent = getIntent();
        alarm = getAlarm(intent.getStringExtra(Alarm.ALRM_INTENT_ID));

        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);

        TextView tv = layout.findViewById(R.id.alarmActivityTV);
        tv.setText(alarm.getTimeAsString());

        //TV ANIMATION
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

        // setting ringtone
        ringtone = RingtoneManager.getRingtone(this, alarm.getSound().getUri());
        ringtone.play();
        Log.e("AlarmActivity ZZZ", alarm.getSound().getName());
        Log.e("AlarmActivity ZZZ", alarm.getSound().getUri().toString());

        Button offBtn = layout.findViewById(R.id.alarmActivityOffBtn);
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmOff();
                finish();
            }
        });
    }

    private void setupBg(){
        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);
        layout.setBackground(Utils.getRandomGradientDrawable());

        ArrayList<TextView> tvs = new ArrayList<>();
        tvs.add(layout.findViewById(R.id.alarmActivityBgTv1));
        tvs.add(layout.findViewById(R.id.alarmActivityBgTv2));
        tvs.add(layout.findViewById(R.id.alarmActivityBgTv3));
        tvs.add(layout.findViewById(R.id.alarmActivityBgTv4));
        tvs.add(layout.findViewById(R.id.alarmActivityBgTv5));

        for(TextView tv : tvs){
            String label = "ALARM";
            String concatLabel = "";

            int nOfCopys = 7;
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

    private void alarmOff(){
        alarm.setActive(false);
        vibrator.cancel();
        ringtone.stop();
    }

    private Alarm getAlarm(String id){
        Alarm alarm = null;
        FileManager fileManager = new FileManager(this);
        for(ArrayList<String> params : fileManager.getParamsArray()){
            for(String s : params){
                if (s.length() > Alarm.ALARM_ID_TAG.length() && s.substring(0, Alarm.ALARM_ID_TAG.length()).equals(Alarm.ALARM_ID_TAG)) {
                    if(s.substring(Alarm.ALARM_ID_TAG.length()).equals(id)){
                        alarm = new Alarm(this, params);
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
    }
}
