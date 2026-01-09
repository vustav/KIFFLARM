package com.example.kifflarm.popups;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.alarm.KIFFAlarmManager;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;

public class AlarmPopup extends Popup {
    private Alarm alarm;
    private TextView timeTV;

    private Button soundBtn;

    public AlarmPopup(KIFFLARM kifflarm, KIFFAlarmManager KIFFAlarmManager, AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        super(kifflarm);

        this.alarm = alarm;

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_alarm, null);
        popupView.setBackground(Utils.getRandomGradientDrawable());

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        timeTV = popupView.findViewById(R.id.alarmPopupTV);
        timeTV.setText(alarm.getTimeAsString());
        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetTimePopup(kifflarm, AlarmPopup.this, alarm.getHourAsString(), alarm.getMinuteAsString());
            }
        });

        RelativeLayout tvLayout = popupView.findViewById(R.id.alarmPopupTVLayout);
        tvLayout.setBackground(Utils.getRandomGradientDrawable());

        soundBtn = popupView.findViewById(R.id.alarmPopupToneBtn);
        setSoundBtnTxt(alarm.getSound().getName());
        soundBtn.setOnClickListener(v -> {
            new SoundPopup(kifflarm, this, alarm);
        });

        Button okBtn = popupView.findViewById(R.id.alarmPopupOkBtn);
        okBtn.setOnClickListener(v -> {
            dismiss();
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if(newAlarm) {
                    Utils.insertInArraySorted(KIFFAlarmManager.getAlarms(), alarm);
                    alarm.setActive(true);
                }
                else{
                    //inte implementerad
                    Utils.sortTimes(KIFFAlarmManager.getAlarms());
                }

                //using this since a sort can push everything around
                alarmsAdapter.notifyDataSetChanged();
            }
        });

        showAtLocation(popupWindow);
    }

    /** SET **/
    public void setSoundBtnTxt(String txt){
        soundBtn.setText("SOUND: "+txt);
    }

    public void setTime(int hour, int minute){
        alarm.setTime(hour, minute);
        timeTV.setText(alarm.getTimeAsString());
    }
}

