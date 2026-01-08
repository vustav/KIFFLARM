package com.example.kifflarm;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmActivity;
import com.example.kifflarm.alarm.AlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.drawables.DrawablePlus;

public class MainView {
    private KIFFLARM kifflarm;
    private RelativeLayout layout;

    public MainView(KIFFLARM kifflarm, AlarmManager alarmManager){
        this.kifflarm = kifflarm;

        createLayout();

        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        AlarmsAdapter alarmsAdapter = new AlarmsAdapter(alarmManager);
        recyclerView.setAdapter(alarmsAdapter);

        RelativeLayout addBtn = layout.findViewById(R.id.addAlarmBtn);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);
                alarmManager.openNewAlarmDialog(alarmsAdapter);
            }
        });

        ImageView addIcon = layout.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        Button fireBtn = layout.findViewById(R.id.fireAlarmBtn);
        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(kifflarm, AlarmActivity.class);

                activityIntent.putExtra(Alarm.ALRM_INTENT_ID, Integer.toString(alarmManager.getAlarms().get(0).getId()));

                kifflarm.startActivity(activityIntent);
            }
        });
    }

    private void createLayout(){
        layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_main_view, null);
    }

    public ViewGroup getLayout(){
        return layout;
    }
}
