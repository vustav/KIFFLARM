package com.kiefer.kifflarm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.kiefer.kifflarm.alarm.AlarmCannonNotification;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.alarm.AlarmsTouchHelper;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.profiles.AlarmsAdapterProfileMain;
import com.kiefer.kifflarm.profiles.Profile;
import com.kiefer.kifflarm.profiles.ProfilesPopup;
import com.kiefer.kifflarm.profiles.QuickProfilesTouchHelper;
import com.kiefer.kifflarm.sound.VolumePopup;
import com.kiefer.kifflarm.profiles.ProfilesManager;
import com.kiefer.kifflarm.profiles.QuickProfilesAdapter;
import com.kiefer.kifflarm.sound.SoundManager;
import com.kiefer.kifflarm.utils.Utils;

public class KIFFLARM extends AppCompatActivity {
    private SoundManager soundManager;
    private FileManager fileManager;
    private ProfilesManager profilesManager;
    private RelativeLayout layout;
    private AlarmManager alarmManager;
    private AlarmsAdapter alarmsAdapter;
    private AlarmsAdapterProfileMain profileAlarmsAdapter;
    private QuickProfilesAdapter quickProfilesAdapter;
    private RelativeLayout profileLblLayout;
    private TextView profileLblTV;
    private Button profileLblDelBtn;
    //private ArrayList<Alarm> alarms;
    private final boolean SHOW_TRIGGER = false;

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
        fileManager = new FileManager(this);
        profilesManager = new ProfilesManager(this);
        alarmManager = new AlarmManager(this, getResources().getString(R.string.custom_alarms_folder));
        soundManager = new SoundManager(this);

        setupLayout();

        checkPermissions();

        //this ensures the layout is ready when the popup is created
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                checkVolume(layout);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e("KIFFLARM ZZZ", "onResume");

        /*
        if an alarm goes off when the main Activity is running nothing in it it gets updated .This
        means that the toggle on the alarm that just went of will still be on. Since the alarm is its
        own it can only save the change, not update it directly, so the get it we need to reload alarms
        and update the adapter
         */
        //loadAlarms(); //load here instead of onCreate since turning an alarm off in AlarmActivity does not update alarms here, they are saved there and needs to be reloaded here
        alarmManager.loadAlarms(fileManager);
        profilesManager.loadProfiles(fileManager);

        if(alarmsAdapter != null){
            alarmsAdapter.onResume();
        }

        if(ongoingAlarm != null){
            Log.e("KIFFLARM ZZZ", "ongoing: "+ongoingAlarm.getId());
        }
        else{
            Log.e("KIFFLARM ZZZ", "NO ongoing");
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
        Log.e("KIFFLARM ZZZ", "askPermission");
        // Should we show an explanation?
        //if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

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
        //}
    }

    boolean volumeWarned = false; //without this you get two popups. Probably something with getViewTreeObserver
    private void checkVolume(ViewGroup layout){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        float volume = ((float) currentVolume) / maxVolume;

        if(volume < .5f){
            if(!volumeWarned) {
                new VolumePopup(KIFFLARM.this);
                volumeWarned = true;
            }
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

        RelativeLayout profilesBg = layout.findViewById(R.id.profilesBg);
        int profilesBgColor1 = Utils.getRandomColor();
        profilesBg.setBackground(Utils.getGradientDrawable(profilesBgColor1, Utils.getRandomColor(), Utils.HORIZONTAL));

        //NICE BG
        TextView bgTVtv = layout.findViewById(R.id.mainBgTV);
        Utils.createNiceBg(layout, bgTVtv, 100);

        //PROFILES
        layout.findViewById(R.id.profilesMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProfilesPopup(KIFFLARM.this, profilesManager);
                Log.e("KIFFLARM ZZZ", "KIFFLARMppppp");
            }
        });

        layout.findViewById(R.id.profilesDivider).setBackgroundColor(Utils.getContrastColor(profilesBgColor1));

        //set up the quick recyclerView
        RecyclerView quickRecyclerView = layout.findViewById(R.id.quickProfilesRecyclerView);
        quickRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        quickProfilesAdapter = new QuickProfilesAdapter(this, quickRecyclerView, profilesManager);
        quickRecyclerView.setAdapter(quickProfilesAdapter);

        QuickProfilesTouchHelper quickTouchHelper = new QuickProfilesTouchHelper(quickProfilesAdapter, profilesManager);
        ItemTouchHelper quickHelper = new ItemTouchHelper(quickTouchHelper);
        quickHelper.attachToRecyclerView(quickRecyclerView);

        //PROFILE ALARMS RECYCLER
        RecyclerView profileAlarmsRecyclerView = layout.findViewById(R.id.profileAlarmsRecyclerView);
        profileAlarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        profileAlarmsAdapter = new AlarmsAdapterProfileMain(this, profilesManager);
        profileAlarmsRecyclerView.setAdapter(profileAlarmsAdapter);

        //PROFILE LBL
        profileLblLayout = layout.findViewById(R.id.profilesLblLayout);
        profileLblTV = layout.findViewById(R.id.profileLblTV);
        profileLblDelBtn = layout.findViewById(R.id.profileLblDelBtn);
        profileLblDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("KIFFLARM ZZZ", "deactivate");
                profilesManager.deactivateAllProfiles();
                updateProfilesUI();
            }
        });

        //updateProfilesUI(); //no use doing this update here since profiles are loaded in onResume, whicj hasn't happened yet

        /*
        if(profilesManager.getActiveProfile() != null){
            setProfileLbl(profilesManager.getActiveProfile());
        }
        else{
           enableProfileLbl(false);
        }

         */

        //AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(profileAlarmsAdapter);
        //ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        //helper.attachToRecyclerView(profileAlarmsRecyclerView);

        //CUSTOM ALARMS RECYCLER
        RecyclerView alarmsRecyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        alarmsAdapter = new AlarmsAdapter(this, alarmManager);
        alarmsRecyclerView.setAdapter(alarmsAdapter);

        AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(alarmsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        helper.attachToRecyclerView(alarmsRecyclerView);

        //ADD
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

        //TEST ALARM
        Button shortAlarmBtn = layout.findViewById(R.id.createShortAlarmBtn);
        if(SHOW_TRIGGER) {
            shortAlarmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TRIGGER ALARM
                    Intent intent = new Intent(KIFFLARM.this, AlarmActivity.class);
                    intent.putExtra(Alarm.ALRM_ID_TAG, Integer.toString(alarmManager.getAlarms().get(0).getId()));

                    //new AlarmCannonActivity(KIFFLARM.this, intent);
                    new AlarmCannonNotification(KIFFLARM.this, intent);
                }
            });
        }
        else{
            shortAlarmBtn.setVisibility(View.INVISIBLE);
        }
    }

    public void updateProfilesUI(){
        Log.e("KIFFLARM ZZZ", "updateUI, profilesManager == null: "+(profilesManager == null));
        quickProfilesAdapter.notifyDataSetChanged();
        profileAlarmsAdapter.notifyDataSetChanged();

        if(profilesManager.getActiveProfile() != null){
            enableProfileLbl(true);
            setProfileLbl(profilesManager.getActiveProfile());
        }
        else{
            enableProfileLbl(false);
        }
    }
    public void setProfileLbl(Profile profile){
        profileLblTV.setText(profile.getName());

    }
    public void enableProfileLbl(boolean enable){
        if(enable){
            profileLblLayout.setVisibility(View.VISIBLE);
        }
        else{
            profileLblLayout.setVisibility(View.INVISIBLE);
        }
    }

    /** GET **/
    public FileManager getFileManager() {
        return fileManager;
    }
/*
    public QuickProfilesAdapter getQuickProfilesAdapter() {
        return quickProfilesAdapter;
    }

 */

    public AlarmsAdapter getAlarmsAdapter() {
        return alarmsAdapter;
    }

    public AlarmsAdapter getProfileAlarmsAdapter() {
        return profileAlarmsAdapter;
    }



    public SoundManager getSoundManager() {
        return soundManager;
    }

    public RelativeLayout getLayout(){
        return layout;
    }

    /** ONGOING ALARM **/
    private static Alarm ongoingAlarm;
    public static void setOngoingAlarm(Alarm alarm){
        ongoingAlarm = alarm;
    }
    public static void resetOngoingAlarm(){
        ongoingAlarm = null;
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