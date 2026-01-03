package com.example.kifflarm;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class KIFFLARM extends AppCompatActivity {
    private AlarmManager alarmManager;
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
        alarmManager = new AlarmManager(this);
        mainView = new MainView(this, alarmManager);

        //add main layout
        ViewGroup layout = findViewById(R.id.main);
        layout.addView(mainView.getLayout());
    }

    public ConstraintLayout getLayout(){
        return layout;
    }
}