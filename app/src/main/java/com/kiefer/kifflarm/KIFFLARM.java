package com.kiefer.kifflarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmActivity;
import com.kiefer.kifflarm.alarm.AlarmCannon;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.alarm.AlarmsTouchHelper;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.sound.SoundManager;
import com.kiefer.kifflarm.utils.Utils;

import java.util.ArrayList;

public class KIFFLARM extends AppCompatActivity {
    private SoundManager soundManager;
    private FileManager fileManager;
    private RelativeLayout layout;
    private AlarmsAdapter alarmsAdapter;
    private ArrayList<Alarm> alarms;

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

        setupLayout();

        soundManager = new SoundManager(this);
        fileManager = new FileManager(this);

        //loadAlarms();

        checkPermissions();
    }

    @Override
    public void onResume(){
        super.onResume();

        /*
        if an alarm goes off when the main Activity is running nothing in it it gets updated .This
        means that the toggle on the alarm that just went of will still be on. Since the alarm is its
        own it can only save the change, not update it directly, so the get it we need to reload alarms
        and update the adapter
         */
        loadAlarms();
        if(getAlarmsAdapter() != null){
            getAlarmsAdapter().onResume();
        }
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

    /** LAYOUT **/

    private void setupLayout(){
        //layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_main_view, null);

        layout = findViewById(R.id.main);

        TextView bgTVtv = layout.findViewById(R.id.mainBgTV);
        Utils.createNiceBg(layout, bgTVtv, 65);

        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmsAdapter = new AlarmsAdapter(this);
        recyclerView.setAdapter(alarmsAdapter);

        AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(alarmsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        helper.attachToRecyclerView(recyclerView);

        RelativeLayout addBtn = layout.findViewById(R.id.addAlarmBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);

                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    askPermission();
                }
                else {
                    alarmsAdapter.openNewAlarmDialog(alarmsAdapter);
                }
            }
        });

        ImageView addIcon = layout.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        Button shortAlarmBtn = layout.findViewById(R.id.createShortAlarmBtn);
        shortAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TRIGGER ALARM
                Intent intent = new Intent(KIFFLARM.this, AlarmActivity.class);
                intent.putExtra(Alarm.ALRM_INTENT_ID, Integer.toString(getAlarms().get(0).getId()));

                new AlarmCannon(KIFFLARM.this, intent);

                //ACTIVE CHECK
                /*
                Log.e("KIFFLARM ZZZ","----------------------------");
                for(Alarm a : alarms){
                    Log.e("KIFFLARM ZZZ","alarm "+alarms.indexOf(a)+", active: "+a.isActive());
                }
                Log.e("KIFFLARM ZZZ","----------------------------");

                 */
            }
        });
    }

    /** ALARMS **/
    public void loadAlarms(){
        alarms = new ArrayList<>();

        //try {

        //recreate saved alarms if there are any
        ArrayList<ArrayList<String>> paramsArray = fileManager.getParamsArray();
        //ArrayList<ArrayList<Alarm.Param>> paramsArray = new ArrayList<>();
        if(!paramsArray.isEmpty()){
            for(ArrayList<String> params : paramsArray){
                alarms.add(new Alarm(this, params));
            }
        }
            /*
        }
        catch (Exception e){
            Log.e("KIFFLARM ZZZ", "loadAlarms, "+e);
        }

             */

        Utils.sortAlarms(alarms);
    }

    public void removeAlarm(int index){
        alarms.remove(index).removeAlarm();
    }

    /** GET **/
    public AlarmsAdapter getAlarmsAdapter() {
        return alarmsAdapter;
    }
    public SoundManager getSoundManager() {
        return soundManager;
    }

    public RelativeLayout getLayout(){
        return layout;
    }

    public ArrayList<Alarm> getAlarms(){
        return alarms;
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