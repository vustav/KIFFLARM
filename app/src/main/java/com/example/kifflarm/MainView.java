package com.example.kifflarm;

import android.content.Intent;
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
import com.example.kifflarm.alarm.kiffAlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.alarm.AlarmsTouchHelper;
import com.example.kifflarm.drawables.DrawablePlus;

public class MainView {
    private KIFFLARM kifflarm;
    private RelativeLayout layout;

    private AlarmsAdapter alarmsAdapter;

    public MainView(KIFFLARM kifflarm, kiffAlarmManager KIFFAlarmManager){
        this.kifflarm = kifflarm;

        createLayout();

        RecyclerView recyclerView = layout.findViewById(R.id.alarmsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        alarmsAdapter = new AlarmsAdapter(KIFFAlarmManager);
        recyclerView.setAdapter(alarmsAdapter);

        AlarmsTouchHelper touchHelper = new AlarmsTouchHelper(alarmsAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        helper.attachToRecyclerView(recyclerView);

        RelativeLayout addBtn = layout.findViewById(R.id.addAlarmBtn);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);
                KIFFAlarmManager.openNewAlarmDialog(alarmsAdapter);
            }
        });

        ImageView addIcon = layout.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        Button fireBtn = layout.findViewById(R.id.fireAlarmBtn);
        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityIntent = new Intent(kifflarm, AlarmActivity.class);

                activityIntent.putExtra(Alarm.ALRM_INTENT_ID, Integer.toString(KIFFAlarmManager.getAlarms().get(0).getId()));

                kifflarm.startActivity(activityIntent);
            }
        });
    }

    public void onResume(){
        if(alarmsAdapter != null){
            alarmsAdapter.onResume();
        }
    }

    private void createLayout(){
        layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_main_view, null);

        TextView bgTVtv = layout.findViewById(R.id.mainBgTV);
        Utils.setupBg(layout, bgTVtv);
    }

    public ViewGroup getLayout(){
        return layout;
    }
}
