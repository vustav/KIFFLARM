package com.example.kifflarm.popups;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;

public class SetTimePopup extends Popup {
    private EditText hourET, minuteET, selectedET;
    private int selectedTxt;

    public SetTimePopup(KIFFLARM kifflarm, AlarmSettingsPopup alarmSettingsPopup, String hour, String minute){
        super(kifflarm);

        selectedTxt = Utils.getRandomColor();

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_set_time, null);
        popupView.setBackground(Utils.getRandomGradientDrawable());

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up hour EditText
        hourET = popupView.findViewById(R.id.setTimePopupHourET);
        hourET.setText(hour);

        //a touchListener to activate the EditText on click
        hourET.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectET(hourET);
            }
            return false;
        });

        //change to 23 if input is more
        hourET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Code to execute before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Code to execute when text is changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    //if(Integer.parseInt(s.toString()) == 24){
                    //    hourET.setText("00");
                    //}
                    if (Integer.parseInt(s.toString()) > 23) {
                        hourET.setText("23");
                    }
                    alarmSettingsPopup.setTime(Integer.parseInt(hourET.getText().toString()), Integer.parseInt(minuteET.getText().toString()));
                }
            }
        });

        //minue ET
        minuteET = popupView.findViewById(R.id.setTimePopupMinuteET);
        minuteET.setText(minute);
        minuteET.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                selectET(minuteET);
            }
            return false;
        });

        minuteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Code to execute before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Code to execute when text is changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > 59) {
                        minuteET.setText("59");
                    }
                    alarmSettingsPopup.setTime(Integer.parseInt(hourET.getText().toString()), Integer.parseInt(minuteET.getText().toString()));
                }
            }
        });
        //start with hour selected
        selectET(hourET);

        LinearLayout tvLayout = popupView.findViewById(R.id.setTimePopupTvsLayout);
        tvLayout.setBackground(Utils.getRandomGradientDrawable());

        //okBtn
        Button okBtn = popupView.findViewById(R.id.setTimeOKBtn);
        okBtn.setOnClickListener(v -> {
            dismiss();
        });

        showAtLocation(popupWindow);
    }

    private void selectET(EditText et){
        selectedET = et;

        if(selectedET == hourET){
            //hourET.setTextColor(kifflarm.getResources().getColor(R.color.alarmsVHbg, null));
            //hourET.setBackgroundColor(kifflarm.getResources().getColor(R.color.selected_bg, null));
            hourET.setTextColor(selectedTxt);
            hourET.setBackgroundColor(Utils.getContrastColor(selectedTxt));

            minuteET.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            minuteET.setBackgroundColor(Color.TRANSPARENT);
        }
        else{
            //minuteET.setTextColor(kifflarm.getResources().getColor(R.color.alarmsVHbg, null));
            //minuteET.setBackgroundColor(kifflarm.getResources().getColor(R.color.selected_bg, null));
            minuteET.setTextColor(selectedTxt);
            minuteET.setBackgroundColor(Utils.getContrastColor(selectedTxt));

            hourET.setTextColor(kifflarm.getResources().getColor(R.color.defaultTxtColor, null));
            hourET.setBackgroundColor(Color.TRANSPARENT);
        }

        //klocka
    }
}


