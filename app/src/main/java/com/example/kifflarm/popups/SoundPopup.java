package com.example.kifflarm.popups;

import android.graphics.Point;
import android.view.Display;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;
import com.example.kifflarm.alarm.AlarmsAdapter;
import com.example.kifflarm.sound.SoundManager;

public class SoundPopup extends Popup {
    private SoundManager soundManager;
    private Alarm alarm;
    private Button soundBtn;

    public SoundPopup(KIFFLARM kifflarm, AlarmPopup alarmPopup, Alarm alarm){
        super(kifflarm);

        soundManager = kifflarm.getSoundManager();

        this.alarm = alarm;

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_sound, null);
        popupView.setBackground(Utils.getRandomGradientDrawable());

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set a margin on the window since we want to max its size
        Display display = kifflarm.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        popupWindow.setWidth(size.x-size.x/5);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the recyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.popupSoundsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        SoundPopupAdapter soundPopupAdapter = new SoundPopupAdapter(soundManager, alarmPopup, this, alarm);
        recyclerView.setAdapter(soundPopupAdapter);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //
            }
        });

        showAtLocation(popupWindow);
    }
}

