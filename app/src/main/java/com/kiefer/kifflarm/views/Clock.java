package com.kiefer.kifflarm.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.popups.SetAlarmPopup;

public class Clock extends RelativeLayout {
    private ClockView clockView;

    public Clock(KIFFLARM kifflarm, SetAlarmPopup setAlarmPopup){
        super(kifflarm);

        FrameLayout layout = (FrameLayout) kifflarm.getLayoutInflater().inflate(R.layout.layout_clock, null);
        addView(layout);

        clockView = new ClockView(kifflarm, setAlarmPopup);

        FrameLayout clockViewLayout = layout.findViewById(R.id.clockViewLayout);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.clockLayoutSize));
        clockView.setLayoutParams(flp);
        clockViewLayout.addView(clockView);
    }

    public ClockView getClockView() {
        return clockView;
    }
}
