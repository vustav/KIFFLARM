package com.example.kifflarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

        checkPermissions();
    }

    @Override
    public void onResume(){
        super.onResume();
        mainView.onResume();
    }

    /** PERMISSIONS **/
    //if the permission is denied the dialog will not show again...
    public static int POST_NOTIFICATIONS_PERMISSION_CODE = 34564576;
    public void checkPermissions(){
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
    }

    public void askPermission(){
        // Should we show an explanation?
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            // Explain to the user why we need this permission
            new AlertDialog.Builder(this)
                    .setTitle("PERMISSION TO POST NOTIFICATIONS")
                    .setMessage("NEED THIS FOR ALARMS TO WORK")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, POST_NOTIFICATIONS_PERMISSION_CODE);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
        for (int i=0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (permission.equals(Manifest.permission.POST_NOTIFICATIONS ) && grantResult == PackageManager.PERMISSION_GRANTED) {
                //
            } else {
                //
            }
        }
         */
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