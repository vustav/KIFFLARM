package com.kiefer.kifflarm.popups;

import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.Utils;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;
import com.kiefer.kifflarm.views.Clock;
import com.kiefer.kifflarm.views.ClockView;

public class SetAlarmPopup extends Popup {
    private Alarm alarm;
    private Button soundBtn;
    private TextView hourTV, minuteTV;
    private RelativeLayout hourLayout, minuteLayout;
    private int selectedTxt;
    private ClockView clockView;

    private int color;
    public static int TIME_UNIT, HOUR = 0, MINUTE = 1;

    public SetAlarmPopup(KIFFLARM kifflarm, AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        super(kifflarm);

        this.alarm = alarm;

        selectedTxt = Utils.getRandomColor();

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_set_alarm, null);
        color = Utils.getRandomColor();
        popupView.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up hour TV
        hourLayout = popupView.findViewById(R.id.setAlarmPopupHourLayout);

        hourTV = popupView.findViewById(R.id.setAlarmPopupHourET);
        setHourTvTxt(alarm.getHourAsString());

        //a touchListener to activate the EditText on click
        hourTV.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectTimeUnit(HOUR);
            }
            return false;
        });

        //minue TV
        minuteLayout = popupView.findViewById(R.id.setAlarmPopupMinuteLayout);
        minuteTV = popupView.findViewById(R.id.setAlarmPopupMinuteET);
        minuteTV.setText(alarm.getMinuteAsString());
        minuteTV.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectTimeUnit(MINUTE);
            }
            return false;
        });

        //start with hour selected
        selectTimeUnit(HOUR);

        //CLOCK
        RelativeLayout clockLayout = popupView.findViewById(R.id.clockLayout);

        Clock clock = new Clock(kifflarm, this);
        clockView = clock.getClockView();
        clockLayout.addView(clock);

        LinearLayout tvLayout = popupView.findViewById(R.id.setAlarmPopupTvsLayout);
        tvLayout.setBackground(Utils.getRandomGradientDrawable());

        SwitchMaterial toggle = popupView.findViewById(R.id.setAlarmPopupToggle);
        toggle.setChecked(alarm.isActive());
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //alarm.activate(isChecked, false); //this is done when ok-btn is presed
            Utils.performHapticFeedback(toggle);
        });

        soundBtn = popupView.findViewById(R.id.setAlarmPopupSoundBtn);
        soundBtn.setOnClickListener(v -> {
            new SoundPopup(kifflarm, this);
        });
        setSoundBtnTxt(alarm.getSound().getName());

        //okBtn
        Button okBtn = popupView.findViewById(R.id.setTimeOKBtn);
        okBtn.setOnClickListener(v -> {
            alarm.setTime(Integer.parseInt(String.valueOf(hourTV.getText())), Integer.parseInt(String.valueOf(minuteTV.getText())));
            alarm.activate(toggle.isChecked(), false);
            alarm.saveAndSchedule();

            if(newAlarm) {
                Utils.insertAlarm(kifflarm.getAlarms(), alarm);
            }
            else{
                Utils.sortAlarms(kifflarm.getAlarms());
            }

            //using this since a sort can push everything around
            alarmsAdapter.notifyDataSetChanged();

            dismiss();
        });

        //cancelBtn
        Button cancelBtn = popupView.findViewById(R.id.setTimeCancelBtn);
        cancelBtn.setOnClickListener(v -> {
            dismiss();
        });

        showAtLocation(popupWindow);
    }

    private void selectTimeUnit(int unit){
        if(unit == HOUR){
            TIME_UNIT = HOUR;

            hourTV.setTextColor(selectedTxt);
            hourLayout.setBackgroundColor(Utils.getContrastColor(selectedTxt));

            minuteTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            minuteLayout.setBackgroundColor(Color.TRANSPARENT);

            if(clockView != null) {
                clockView.updateTimeUnit();
            }
        }
        else{
            TIME_UNIT = MINUTE;

            minuteTV.setTextColor(selectedTxt);
            minuteLayout.setBackgroundColor(Utils.getContrastColor(selectedTxt));

            hourTV.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            hourLayout.setBackgroundColor(Color.TRANSPARENT);

            if(clockView != null) {
                clockView.updateTimeUnit();
            }
        }
    }

    public void switchTimeUnit(){
        if(TIME_UNIT == HOUR){
            selectTimeUnit(MINUTE);
        }
        else{
            selectTimeUnit(HOUR);
        }
    }

    /** SET **/
    private void setTime(int hour, int minute){
        alarm.setTime(hour, minute);
        setHourTvTxt(alarm.getHourAsString());
        setMinuteTvTxt(alarm.getMinuteAsString());
        //updateAdapter();
    }

    public void setHour(int hour){
        //alarm.setHour(hour);
        setHourTvTxt(Utils.timeToString(hour));
    }

    public void setMinute(int minute){
        //alarm.setMinute(minute);
        setMinuteTvTxt(Utils.timeToString(minute));
    }

    public void setHourTvTxt(String txt){
        hourTV.setText(txt);
    }

    public void setMinuteTvTxt(String txt){
        minuteTV.setText(txt);
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
        return TIME_UNIT;
    }

    public int getHour(){
        return alarm.getHour();
    }

    public int getMinute(){
        return alarm.getMinute();
    }
}


