package com.kiefer.kifflarm.profiles;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.alarm.SetAlarmPopup;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;

public class EditProfilePopup extends Popup {
    private Profile profile;
    public EditProfilePopup(KIFFLARM kifflarm, ProfilesManager profilesManager, ProfilesListPopup profilesListPopup, Profile profile, boolean newProfile){
        super(kifflarm);
        this.profile = profile;

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_edit_profile, null);

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
        popupWindow.setHeight(size.y - size.y/4);

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.newProfilePopupBg);
        TextView bgTv = popupView.findViewById(R.id.newProfilePopupBgTV);
        Utils.createNiceBg(bg, bgTv, 70);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //BG
        int bgColor1 = Utils.getRandomColor();
        popupView.findViewById(R.id.profilesBg).setBackground(Utils.getGradientDrawable(bgColor1, Utils.getRandomColor(), Utils.HORIZONTAL));

        //NAME
        TextView nameTV = popupView.findViewById(R.id.profileNameTV);
        nameTV.setTextColor(Utils.getContrastColor(bgColor1));

        EditText nameEt = popupView.findViewById(R.id.profileNameET);
        nameEt.setText(profile.getName());
        nameEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                profile.setName(s.toString());
            }
        });

        //LABEL
        TextView shortLblTV = popupView.findViewById(R.id.profileShortLabelTV);
        shortLblTV.setTextColor(Utils.getContrastColor(bgColor1));

        EditText shortLblEt = popupView.findViewById(R.id.profileShortLabelET);
        shortLblEt.setText(profile.getShortLabel());
        shortLblEt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                profile.setShortLabel(s.toString());
            }
        });

        //ICON
        popupView.findViewById(R.id.profileIconBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProfileIconPopup(kifflarm, profile, popupView.findViewById(R.id.profileIconBtnIcon));
            }
        });
        popupView.findViewById(R.id.profileIconBtnIcon).setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), profile.getIconId(), null));

        //QUICK
        popupView.findViewById(R.id.profileQuickBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(popupView);
                profile.setQuick(!profile.isQuick());
                updateDesktopIndicator(profile.isQuick(), popupView.findViewById(R.id.profileQuickIndicator));
            }
        });
        updateDesktopIndicator(profile.isQuick(), popupView.findViewById(R.id.profileQuickIndicator));

        //set up the recyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.newProfilePopupRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        AlarmsAdapterProfile alarmsAdapter = new AlarmsAdapterProfile(kifflarm, profile.getAlarmManager());
        recyclerView.setAdapter(alarmsAdapter);

        //ADD ALARM
        RelativeLayout addBtn = popupView.findViewById(R.id.addAlarmBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);
                if (kifflarm.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    kifflarm.askPermission();
                }
                else {
                    alarmsAdapter.openNewAlarmDialog(alarmsAdapter);
                }
            }
        });

        ImageView addIcon = popupView.findViewById(R.id.addAlarmIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        //okBtn
        Button okBtn = popupView.findViewById(R.id.newProfileOKBtn);
        okBtn.setOnClickListener(v -> {
            if(newProfile) {
                profilesManager.addProfile(profile);
                profilesListPopup.insertLastInAdapter();
            }
            else{
                profilesListPopup.notifyAdapter();
            }

            //this is if it's added to quick from here. Can't do it on button click since it isn't added to peofilemanager yet if it's a new profile. Could be better.
            kifflarm.getQuickProfilesAdapter().notifyDataSetChanged();
            dismiss();
        });

        //cancelBtn
        Button cancelBtn = popupView.findViewById(R.id.newProfileCancelBtn);
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        showAtLocation(popupWindow);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //
            }
        });
    }

    private void updateDesktopIndicator(boolean on, FrameLayout indicator){
        if(on) {
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOn, null));
        }
        else{
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOff, null));
        }
    }
}
