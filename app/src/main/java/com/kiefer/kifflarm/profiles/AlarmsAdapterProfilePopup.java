package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.alarm.AlarmsAdapter;

public class AlarmsAdapterProfilePopup extends AlarmsAdapter {

    public AlarmsAdapterProfilePopup(KIFFLARM kifflarm, AlarmManager alarmManager){
        super(kifflarm, alarmManager);
    }
    @Override
    public void notifyItemInsertedLocal(int index, Alarm alarm){
        alarm.activate(false);
        super.notifyItemInserted(index);
    }
}
