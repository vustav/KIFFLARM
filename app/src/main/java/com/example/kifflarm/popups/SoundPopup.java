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

public class SoundPopup extends Popup {
    private Alarm alarm;
    private Button soundBtn;

    private boolean newAlarm;

    public SoundPopup(KIFFLARM kifflarm, Alarm alarm, Button soundBtn){
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

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //
            }
        });

        showAtLocation(popupWindow);
    }
}

