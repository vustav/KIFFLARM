package com.kiefer.kifflarm.profiles;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.views.CSeekBar;

import java.util.Locale;

public class ProfilesPopup extends Popup {
    public ProfilesPopup(KIFFLARM kifflarm){
        super(kifflarm);
        Log.e("ZZZ", " ProfilesPOPUO ppppp");


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
        //popupWindow.setHeight(height);

        //popupWindow.setWidth((int) kifflarm.getResources().getDimension(R.dimen.volumePopupWidth));
        //popupWindow.setHeight((int) kifflarm.getResources().getDimension(R.dimen.volumePopupHeight));

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.profilesPopupBg);
        TextView bgTv = popupView.findViewById(R.id.profilesPopupBgTV);
        Utils.createNiceBg(bg, bgTv, 70);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //content

        //ADD
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
    }
}
