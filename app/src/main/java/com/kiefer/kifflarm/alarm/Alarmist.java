package com.kiefer.kifflarm.alarm;

import java.util.ArrayList;

public interface Alarmist {
    Alarm getAlarm(int index);
    ArrayList<Alarm> getAlarms();
    void removeAlarm(int index);
    int getItemCount();
    void sortAlarms();
}
