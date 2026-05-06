package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;

public class AlarmsAdapterProfileMain extends AlarmsAdapter {
    public AlarmsAdapterProfileMain(KIFFLARM kifflarm, ProfilesManager profilesManager){
        super(kifflarm, profilesManager);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);

        viewHolder.mainLayout.setPadding(0, 0, 0, 0);
    }

}
