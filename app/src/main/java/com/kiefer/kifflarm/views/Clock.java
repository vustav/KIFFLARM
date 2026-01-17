package com.kiefer.kifflarm.views;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.popups.SetAlarmPopup;

public class Clock extends RelativeLayout {
    private ClockView clockView;

    public Clock(KIFFLARM kifflarm, SetAlarmPopup setAlarmPopup){
        super(kifflarm);

        RelativeLayout layout = (RelativeLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_clock, null);
        addView(layout);

        clockView = new ClockView(kifflarm, setAlarmPopup);

        FrameLayout clockViewLayout = layout.findViewById(R.id.clockViewLayout);
        clockViewLayout.addView(clockView);
    }

    public ClockView getClockView() {
        return clockView;
    }
}
