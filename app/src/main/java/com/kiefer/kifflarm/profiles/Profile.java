package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Profile {
    private AlarmManager alarmManager;
    private String name, shortLabel;
    private boolean quick;
    private int iconId;
private String folder;
    private int id;

    public Profile(KIFFLARM kifflarm){
        Random r = new Random();
        name = "";
        shortLabel = "";
        quick = false;
        iconId = Utils.getRandomNoteIconId();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        id = (int) date.getTime();

        folder = kifflarm.getResources().getString(R.string.profiles_folder) + "/" + getId();

        alarmManager = new AlarmManager(kifflarm, getFolder());
    }

    /** ALARMS **/

    /** GET **/
    public String getName() {
        return name;
    }
    public String getShortLabel(){
        return shortLabel;
    }
    public boolean isQuick() {
        return quick;
    }
    public int getIconId() {
        return iconId;
    }

    public int getId() {
        return id;
    }

    public String getFolder() {
        return folder;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    /** SET **/
    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public void setName(String name) {
        this.name = name;
    }
}
