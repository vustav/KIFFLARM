package com.kiefer.kifflarm.popups;

import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.views.CSeekBar;

import java.util.Locale;

public class VolumePopup extends Popup{
    public VolumePopup(KIFFLARM kifflarm){
        super(kifflarm);

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_volume, null);

        //create the popupWindow
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set a margin on the window since we want to max its size
        Display display = this.kifflarm.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //popupWindow.setWidth(size.x-size.x/2);
        //popupWindow.setHeight(size.y/3);
        popupWindow.setWidth((int) kifflarm.getResources().getDimension(R.dimen.volumePopupWidth));
        popupWindow.setHeight((int) kifflarm.getResources().getDimension(R.dimen.volumePopupHeight));

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.volumePopupBg);
        TextView bgTv = popupView.findViewById(R.id.volumePopupBgTV);
        Utils.createNiceBg(bg, bgTv, 45);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the txt
        FrameLayout textContainer = popupView.findViewById(R.id.volumePopupTextLayout);
        TextView mainTV = popupView.findViewById(R.id.volumePopupMainTV);
        int color = Utils.getRandomColor();
        textContainer.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));
        mainTV.setTextColor(Utils.getContrastColor(color));

        //get volumes
        AudioManager audioManager = (AudioManager) kifflarm.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        float volume = ((float) currentVolume) / maxVolume;

        CSeekBar seekBar = new CSeekBar(kifflarm, CSeekBar.VERTICAL_DOWN_UP);
        seekBar.setColors(ContextCompat.getColor(kifflarm, R.color.indicatorOn), ContextCompat.getColor(kifflarm, R.color.black));
        seekBar.setThumb(false);
        seekBar.setProgress(volume);

        float newVolPerc = .9f;
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        seekBar.onTouchEvent(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        seekBar.onTouchEvent(event);

                        int newVolume = (int)((float)maxVolume * seekBar.getProgress());
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0);
                        break;
                    case MotionEvent.ACTION_UP:
                        seekBar.onTouchEvent(event);

                        newVolume = (int)((float)maxVolume * seekBar.getProgress());
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0);

                        if(newVolume >= (int)((float)maxVolume * newVolPerc)){
                            dismiss();
                        }
                        break;
                }
                return true;
            }
        });

        Button raiseBtn = popupView.findViewById(R.id.volumePopupRaiseBtn);
        raiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newVolume = (int)((float)maxVolume * newVolPerc);

                int duration = 500;
                int ticks = 10;
                new CountDownTimer(duration, duration/ticks) {
                    public void onTick(long millisUntilFinished) {
                        float perc = ((float) duration - (float) millisUntilFinished)/(float) duration;
                        seekBar.setProgress(newVolPerc * perc);
                    }
                    public void onFinish() {
                        seekBar.setProgress(newVolPerc);
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0);
                        dismiss();
                    }
                }.start();
            }
        });

        Button cancelBtn = popupView.findViewById(R.id.volumePopupCancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        FrameLayout seekBarContainer = popupView.findViewById(R.id.volumePopupSeekContainer);
        seekBarContainer.addView(seekBar);

        showAtLocation(popupWindow);
    }
}
