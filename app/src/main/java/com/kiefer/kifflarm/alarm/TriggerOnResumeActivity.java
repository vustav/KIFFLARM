package com.kiefer.kifflarm.alarm;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class TriggerOnResumeActivity extends AppCompatActivity {

    /** this activity exists to sole a little edge case:
     *
     * when an alarm goes off and the main activity is open, and the user swipes to turn off the alarm,
     * onResume will never be called and the recyclerView will not be updated to show the recent
     * alarm as deactivated. Starting and immediately closing this activity solves that.**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TriggerOnResumeActivity ZZZ", "gogogogogogo");
        finish();
    }
}
