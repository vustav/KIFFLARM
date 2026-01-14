package com.example.kifflarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmActivity;
import com.example.kifflarm.alarm.AlarmCannon;
import com.example.kifflarm.alarm.kiffAlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.alarm.AlarmsTouchHelper;
import com.example.kifflarm.drawables.DrawablePlus;

public class MainView {
    private KIFFLARM kifflarm;
    private kiffAlarmManager kiffAlarmManager;
    private RelativeLayout layout;
    private AlarmsAdapter alarmsAdapter;

    public MainView(KIFFLARM kifflarm, kiffAlarmManager kiffAlarmManager){
        this.kifflarm = kifflarm;
        this.kiffAlarmManager = kiffAlarmManager;

        createLayout();

        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        alarmsAdapter = new AlarmsAdapter(kiffAlarmManager);
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

                if (kifflarm.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    kifflarm.askPermission();
                }
                else {
                    kiffAlarmManager.openNewAlarmDialog(alarmsAdapter);
                }
            }
        });

        ImageView addIcon = layout.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        Button shortAlarmBtn = layout.findViewById(R.id.createShortAlarmBtn);
        shortAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(kifflarm, AlarmActivity.class);
                intent.putExtra(Alarm.ALRM_INTENT_ID, Integer.toString(kiffAlarmManager.getAlarms().get(0).getId()));

                new AlarmCannon(kifflarm, intent);
            }
        });
    }

    public void onResume(){

        /*
        if an alarm goes off when the main Activity is running nothing in it it gets updated .This
        means that the toggle on the alarm that just went of will still be on. Since the alarm is its
        own it can only save the change, not update it directly, so the get it we need to reload alarms
        and update the adapter

         */
        kiffAlarmManager.loadAlarms();
        if(alarmsAdapter != null){
            alarmsAdapter.onResume();
        }
    }

    private void createLayout(){
        layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_main_view, null);

        TextView bgTVtv = layout.findViewById(R.id.mainBgTV);
        Utils.createNiceBg(layout, bgTVtv, 65);
    }

    public ViewGroup getLayout(){
        return layout;
    }
}
