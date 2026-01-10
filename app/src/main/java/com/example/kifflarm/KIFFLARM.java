package com.example.kifflarm;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kifflarm.alarm.kiffAlarmManager;
import com.example.kifflarm.sound.SoundManager;

public class KIFFLARM extends AppCompatActivity {
    private kiffAlarmManager kiffAlarmManager;
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
        kiffAlarmManager = new kiffAlarmManager(this);
        mainView = new MainView(this, kiffAlarmManager);

        //add main layout
        ViewGroup layout = findViewById(R.id.main);
        layout.addView(mainView.getLayout());
    }

    @Override
    public void onResume(){
        super.onResume();
        mainView.onResume();
    }

    /** GET **/
    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ConstraintLayout getLayout(){
        return layout;
    }

    /** DESTRUCTION **/
    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}