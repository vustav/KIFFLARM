package com.kiefer.kifflarm.profiles;

import android.widget.RelativeLayout;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;

public class AlarmsAdapterProfilePopup extends AlarmsAdapter {

    public AlarmsAdapterProfilePopup(KIFFLARM kifflarm, AlarmManager alarmManager){
        super(kifflarm, alarmManager);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);

        //in profile alarms are not shown as on or off, so remove the toggle and make all alarms fully visible
        activateVH(viewHolder, true);

        viewHolder.bg.removeView(viewHolder.toggleBtnLayout);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) viewHolder.deleteBtnLayout.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewHolder.deleteBtnLayout.setLayoutParams(rlp);
    }
    @Override
    public void notifyItemInsertedLocal(int index, Alarm alarm){
        alarm.activate(false);
        super.notifyItemInserted(index);
    }
}
