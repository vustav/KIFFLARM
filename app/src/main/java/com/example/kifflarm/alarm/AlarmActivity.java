package com.example.kifflarm.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kifflarm.FileManager;
import com.example.kifflarm.R;

import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {
    private Alarm alarm;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);

        vibrator = ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
        startVibrating();

        Intent intent = getIntent();

        String ringtone = intent.getStringExtra(Alarm.ALRM_INTENT_TONE);
        String id = intent.getStringExtra(Alarm.ALRM_INTENT_ID);

        alarm = getAlarm(id);

        RelativeLayout layout = findViewById(R.id.alarmActivityLayout);

        TextView tv = layout.findViewById(R.id.alarmActivityTV);
        tv.setText(ringtone+", "+id+", "+alarm.getTimeAsString());

        Button btn = layout.findViewById(R.id.alarmActivityBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVibrating();
                deactivateAlarm();
                finish();
            }
        });
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

    private void stopVibrating() {
        vibrator.cancel();
    }

    private void deactivateAlarm(){
        alarm.setActive(false);
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
}
