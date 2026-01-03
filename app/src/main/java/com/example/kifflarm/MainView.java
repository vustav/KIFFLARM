package com.example.kifflarm;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainView {
    private KIFFLARM kifflarm;
    private AlarmManager alarmManager;
    private RelativeLayout layout;

    public MainView(KIFFLARM kiffLarm, AlarmManager alarmManager){
        this.kifflarm = kiffLarm;
        this.alarmManager = alarmManager;

        createLayout();


        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        //recyclerView.layoutManager = new LinearLayoutManager(kiffLarm);
        recyclerView.setLayoutManager(new LinearLayoutManager(kiffLarm));

        AlarmsAdapter alarmsAdapter = new AlarmsAdapter(alarmManager);
        recyclerView.setAdapter(alarmsAdapter);

        ImageView addBtn = layout.findViewById(R.id.addAlarmBtn);
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
