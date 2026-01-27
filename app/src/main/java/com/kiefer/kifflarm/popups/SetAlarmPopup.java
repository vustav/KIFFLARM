package com.kiefer.kifflarm.popups;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.views.Clock;
import com.kiefer.kifflarm.views.ClockView;

public class SetAlarmPopup extends Popup {
    private Alarm alarm;
    private Button soundBtn;
    private TextView hourTV, minuteTV, snoozeTV, timeLabel, snoozeLabel;
    private RelativeLayout hourLayout, minuteLayout, snoozeLayout;
    private int selectedTxtColor, timeGradientTopColor, snoozeGradientTopColor;
    private ClockView clockView;
    private int color;
    public static int CLOCK_VALUE, HOUR = 0, MINUTE = 1, SNOOZE = 2;

    public SetAlarmPopup(KIFFLARM kifflarm, AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        super(kifflarm);

        this.alarm = alarm;

        selectedTxtColor = Utils.getRandomColor();

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_set_alarm, null);
        color = Utils.getRandomColor();
        //popupView.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));

        //create the popupWindow
        DisplayMetrics displayMetrics = new DisplayMetrics();
        kifflarm.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels - displayMetrics.widthPixels/5;
        int height = (int) popupView.getResources().getDimension(R.dimen.clockPopupHeight);

        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.popupSetAlarmBg);
        TextView bgTv = popupView.findViewById(R.id.setAlarmPopupBgTV);
        Utils.createNiceBg(bg, bgTv, 35);

        //set up hour TV
        hourLayout = popupView.findViewById(R.id.setAlarmPopupHourLayout);

        hourTV = popupView.findViewById(R.id.setAlarmPopupHourET);
        setHourTvTxt(alarm.getHourAsString());

        //a touchListener to activate the EditText on click
        hourTV.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectClockValue(HOUR);
            }
            return false;
        });

        //minute TV
        minuteLayout = popupView.findViewById(R.id.setAlarmPopupMinuteLayout);
        minuteTV = popupView.findViewById(R.id.setAlarmPopupMinuteET);
        minuteTV.setText(alarm.getMinuteAsString());
        minuteTV.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectClockValue(MINUTE);
            }
            return false;
        });

        //snooze TV
        snoozeLayout = popupView.findViewById(R.id.setAlarmPopupSnoozeLayout);
        snoozeTV = popupView.findViewById(R.id.setAlarmPopupSnoozeET);
        snoozeTV.setText(alarm.getSnoozeAsString());
        snoozeTV.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectClockValue(SNOOZE);
            }
            return false;
        });

        //set up labels
        timeLabel = popupView.findViewById(R.id.setAlarmPopupTimeLabel);
        snoozeLabel = popupView.findViewById(R.id.setAlarmPopupSnoozeLabel);
        timeLabel.setBackgroundColor(timeGradientTopColor);
        snoozeLabel.setBackgroundColor(snoozeGradientTopColor);

        //start with hour selected
        selectClockValue(HOUR);

        //CLOCK
        RelativeLayout clockLayout = popupView.findViewById(R.id.clockLayout);

        Clock clock = new Clock(kifflarm, this);
        clockView = clock.getClockView();
        clockLayout.addView(clock);

        LinearLayout tvLayout = popupView.findViewById(R.id.setAlarmPopupTvsLayout);
        timeGradientTopColor = Utils.getRandomColor();
        tvLayout.setBackground(Utils.getGradientDrawable(timeGradientTopColor, Utils.getRandomColor(), Utils.VERTICAL));
        FrameLayout snoozeLayout = popupView.findViewById(R.id.setAlarmPopupSnoozeTvLayout);
        snoozeGradientTopColor = Utils.getRandomColor();
        snoozeLayout.setBackground(Utils.getGradientDrawable(snoozeGradientTopColor, Utils.getRandomColor(), Utils.VERTICAL));

        soundBtn = popupView.findViewById(R.id.setAlarmPopupSoundBtn);
        soundBtn.setOnClickListener(v -> {
            new SoundPopup(kifflarm, this);
        });
        setSoundBtnTxt(alarm.getSound().getName());

        //okBtn
        Button okBtn = popupView.findViewById(R.id.setTimeOKBtn);
        okBtn.setOnClickListener(v -> {
            //Log.e("SeAlarmtPopup ZZZ", "ok 0");
            alarm.setTime(Integer.parseInt(String.valueOf(hourTV.getText())), Integer.parseInt(String.valueOf(minuteTV.getText())));
            alarm.setSnoozeTime(Integer.parseInt(String.valueOf(snoozeTV.getText())));

            alarm.activate(true);
            alarm.saveAndSchedule();

            //Log.e("SeAlarmtPopup ZZZ", "ok 1");

            if(newAlarm) {
                alarmsAdapter.notifyItemInsertedLocal(Utils.insertAlarm(kifflarm.getAlarms(), alarm));
            }
            else{
                Utils.sortAlarms(kifflarm.getAlarms());

                //using this since a sort can push everything around
                alarmsAdapter.notifyDataSetChangedLocal();
            }
            //Log.e("SeAlarmtPopup ZZZ", "ok 2");

            dismiss();
            //Log.e("SeAlarmtPopup ZZZ", "ok 4");
        });

        //cancelBtn
        Button cancelBtn = popupView.findViewById(R.id.setTimeCancelBtn);
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        showAtLocation(popupWindow);
    }

    private void selectClockValue(int unit){
        if(unit == HOUR){
            CLOCK_VALUE = HOUR;

            hourTV.setTextColor(selectedTxtColor);
            hourLayout.setBackgroundColor(Utils.getContrastColor(selectedTxtColor));

            minuteTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            minuteLayout.setBackgroundColor(Color.TRANSPARENT);
            snoozeTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            snoozeLayout.setBackgroundColor(Color.TRANSPARENT);

            /*
            timeLabel.setTextColor(selectedTxtColor);
            timeLabel.setBackgroundColor(Utils.getContrastColor(selectedTxtColor));
            snoozeLabel.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            snoozeLabel.setBackgroundColor(snoozeGradientTopColor);

             */

            if(clockView != null) {
                clockView.updateTimeUnit();
            }
        }
        else if(unit == MINUTE){
            CLOCK_VALUE = MINUTE;

            minuteTV.setTextColor(selectedTxtColor);
            minuteLayout.setBackgroundColor(Utils.getContrastColor(selectedTxtColor));

            hourTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            hourLayout.setBackgroundColor(Color.TRANSPARENT);
            snoozeTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            snoozeLayout.setBackgroundColor(Color.TRANSPARENT);

            /*
            timeLabel.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            timeLabel.setBackgroundColor(timeGradientTopColor);
            snoozeLabel.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            snoozeLabel.setBackgroundColor(snoozeGradientTopColor);

             */

            if(clockView != null) {
                clockView.updateTimeUnit();
            }
        }
        else{
            CLOCK_VALUE = SNOOZE;

            snoozeTV.setTextColor(selectedTxtColor);
            snoozeLayout.setBackgroundColor(Utils.getContrastColor(selectedTxtColor));

            hourTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            hourLayout.setBackgroundColor(Color.TRANSPARENT);
            minuteTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            minuteLayout.setBackgroundColor(Color.TRANSPARENT);

            /*
            timeLabel.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            timeLabel.setBackgroundColor(timeGradientTopColor);
            snoozeLabel.setTextColor(selectedTxtColor);
            snoozeLabel.setBackgroundColor(Utils.getContrastColor(selectedTxtColor));

             */

            if(clockView != null) {
                clockView.updateTimeUnit();
            }
        }
    }

    public void setClockValue(int value){
        if(value == HOUR){
            selectClockValue(HOUR);
        }
        else if(value == MINUTE){
            selectClockValue(MINUTE);
        }
        else{
            selectClockValue(SNOOZE);
        }
    }

    /** SET **/

    public void setHour(int hour){
        setHourTvTxt(Utils.timeToString(hour));
    }

    public void setMinute(int minute){
        setMinuteTvTxt(Utils.timeToString(minute));
    }

    public void setSnooze(int snooze){
        setSnoozeTvTxt(Utils.timeToString(snooze));
    }

    public void setHourTvTxt(String txt){
        hourTV.setText(txt);
    }

    public void setMinuteTvTxt(String txt){
        minuteTV.setText(txt);
    }

    public void setSnoozeTvTxt(String txt){
        snoozeTV.setText(txt);
    }

    public void setSoundBtnTxt(String txt){
        soundBtn.setText("SOUND: "+txt);
    }

    /** GET **/
    public int getColor() {
        return color;
    }

    public Alarm getAlarm(){
        return alarm;
    }
    public int getSelectedTimeUint(){
        return CLOCK_VALUE;
    }

    public int getHour(){
        return alarm.getHour();
    }

    public int getMinute(){
        return alarm.getMinute();
    }

    public int getSnoozeTime(){
        return alarm.getSnoozeTime();
    }
}


