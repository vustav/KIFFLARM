package com.example.kifflarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kifflarm.alarm.AlarmManager;
import com.example.kifflarm.sound.SoundManager;

public class KIFFLARM extends AppCompatActivity {
    private AlarmManager alarmManager;
    private SoundManager soundManager;
    private MainView mainView;

    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        layout = findViewById(R.id.main);

        //create classes
        soundManager = new SoundManager(this);
        alarmManager = new AlarmManager(this);
        mainView = new MainView(this, alarmManager);

        //add main layout
        ViewGroup layout = findViewById(R.id.main);
        layout.addView(mainView.getLayout());
    }

    public ConstraintLayout getLayout(){
        return layout;
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String alarmMessage = intent != null ? intent.getStringExtra(Alarm.MESSAGE) : null;

            Toast t = Toast.makeText(context, "ALARMALARMALARMALARMALARMALARMALARM", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public static class BootCompletedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent != null ? intent.getAction() : null)) {
                // Set your alarm here
            }
        }
    }

    /** GET **/
    public SoundManager getSoundManager() {
        return soundManager;
    }
}