package com.example.kifflarm.popups;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.AlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;

public class AlarmPopup extends Popup {
    private AlarmManager alarmManager;
    private AlarmsAdapter alarmsAdapter;
    private Alarm alarm;
    private TextView timeTV;

    private boolean newAlarm;

    public AlarmPopup(KIFFLARM kifflarm, AlarmManager alarmManager, AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        super(kifflarm);

        this.alarmManager = alarmManager;
        this.alarmsAdapter = alarmsAdapter;

        this.alarm = alarm;
        this.newAlarm = newAlarm;

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_alarm, null);
        //popupView.findViewById(R.id.listBgIV).setBackground(ContextCompat.getDrawable(kifflarm, Utils.getRandomImageId()));
        popupView.setBackground(Utils.getRandomGradientDrawable());

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        timeTV= popupView.findViewById(R.id.alarmPopupTV);
        timeTV.setText(alarm.getTimeAsString());
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetTimePopup(kifflarm, AlarmPopup.this, alarm.getHourAsString(), alarm.getMinuteAsString());
            }
        });

        Button okBtn = popupView.findViewById(R.id.alarmPopupOkBtn);
        okBtn.setOnClickListener(v -> {
            dismiss();
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if(newAlarm) {
                    Utils.insertInArraySorted(alarmManager.getAlarms(), alarm);
                    alarm.setActive(true);
                }
                else{
                    //inte implementerad
                    Utils.sortTimes(alarmManager.getAlarms());
                }

                //using this since a sort can push everything around
                alarmsAdapter.notifyDataSetChanged();
            }
        });

        showAtLocation(popupWindow);
    }

    public void setTime(int hour, int minute){
        alarm.setTime(hour, minute);
        timeTV.setText(alarm.getTimeAsString());
    }
}

