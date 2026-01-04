package com.example.kifflarm;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.alarm.AlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;

public class MainView {
    private KIFFLARM kifflarm;
    private RelativeLayout layout;

    public MainView(KIFFLARM kiffLarm, AlarmManager alarmManager){
        this.kifflarm = kiffLarm;

        createLayout();

        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kiffLarm));

        AlarmsAdapter alarmsAdapter = new AlarmsAdapter(alarmManager);
        recyclerView.setAdapter(alarmsAdapter);

        ImageView addBtn = layout.findViewById(R.id.addAlarmBtn);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.openNewAlarmDialog(alarmsAdapter);
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
