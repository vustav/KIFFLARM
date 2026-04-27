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
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;

public class ProfilesPopup extends Popup {
    public ProfilesPopup(KIFFLARM kifflarm, ProfilesManager profilesManager){
        super(kifflarm);

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_profiles, null);

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
        RelativeLayout bg = popupView.findViewById(R.id.profilesPopupBg);
        TextView bgTv = popupView.findViewById(R.id.profilesPopupBgTV);
        Utils.createNiceBg(bg, bgTv, 70);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the recyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.profilesPopupRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        ProfilesPopupAdapter profilesAdapter = new ProfilesPopupAdapter(kifflarm, recyclerView, profilesManager);
        recyclerView.setAdapter(profilesAdapter);

        //ADD PROFILE
        RelativeLayout addBtn = popupView.findViewById(R.id.addProfileBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);

                //permission here or when creating alarm in profile??
                if (kifflarm.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    kifflarm.askPermission();
                }
                else {
                    //new profile
                }
            }
        });

        ImageView addIcon = popupView.findViewById(R.id.addProfileIcon);
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
