package com.kiefer.kifflarm.profiles;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;

public class NewProfilePopup extends Popup {
    public NewProfilePopup(KIFFLARM kifflarm, ProfilesManager profilesManager){
        super(kifflarm);

        Profile profile = new Profile(kifflarm);

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_new_profile, null);

        //create the popupWindow
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set a margin on the window since we want to max its size
        Display display = kifflarm.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        popupWindow.setWidth(size.x-size.x/5);
        popupWindow.setHeight(size.y - size.y/5);

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.newProfilePopupBg);
        TextView bgTv = popupView.findViewById(R.id.newProfilePopupBgTV);
        Utils.createNiceBg(bg, bgTv, 70);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the recyclerView

        RecyclerView recyclerView = popupView.findViewById(R.id.newProfilePopupRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        NewProfileAlarmsAdapter alarmsAdapter = new NewProfileAlarmsAdapter(kifflarm, profile.getAlarmManager());
        recyclerView.setAdapter(alarmsAdapter);

        //ADD ALARM
        RelativeLayout addBtn = popupView.findViewById(R.id.addAlarmBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);

            }
        });

        ImageView addIcon = popupView.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        showAtLocation(popupWindow);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //
            }
        });
    }
}
