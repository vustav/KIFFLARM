package com.kiefer.kifflarm.sound;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.views.CSeekBar;

public class PromptPopup extends Popup {
    public PromptPopup(KIFFLARM kifflarm, String mesage, View.OnClickListener listener){
        super(kifflarm);

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_volume, null);

        //create the popupWindow
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.setWidth((int) kifflarm.getResources().getDimension(R.dimen.volumePopupWidth));
        popupWindow.setHeight((int) kifflarm.getResources().getDimension(R.dimen.volumePopupHeight));

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.volumePopupBg);
        TextView bgTv = popupView.findViewById(R.id.volumePopupBgTV);
        Utils.createNiceBg(bg, bgTv, 50);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //MESSAGE
        TextView tv = popupView.findViewById(R.id.promptPopupMainTV);
        tv.setText(mesage);

        //OK
        Button okBtn = popupView.findViewById(R.id.volumePopupOkBtn);
        okBtn.setOnClickListener(listener);

        Button cancelBtn = popupView.findViewById(R.id.volumePopupCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        showAtLocation(popupWindow);
    }
}
