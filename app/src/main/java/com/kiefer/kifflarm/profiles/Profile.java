package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.alarm.AlarmManager;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;
import com.kiefer.kifflarm.files.Saveable;
import com.kiefer.kifflarm.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Profile implements Saveable {
    private KIFFLARM kifflarm;
    private ProfilesManager profilesManager;
    private AlarmManager alarmManager;
    private FileManager fileManager;
    private String name, shortLabel;
    private boolean quick, active;
    private int iconId;
    private String folder;
    private int id, index, quickIndex;

    public Profile(KIFFLARM kifflarm, ProfilesManager profilesManager, ArrayList<Param> params){
        this(kifflarm, profilesManager);
        this.profilesManager = profilesManager;
        restoreParams(params);

        folder = profilesManager.getProfilesFolder() + "/" + getId();
        alarmManager = new AlarmManager(kifflarm, getFolder());
        alarmManager.loadAlarms(kifflarm.getFileManager());

        activate(active);
    }
    public Profile(KIFFLARM kifflarm, ProfilesManager profilesManager){
        //Random r = new Random();
        //this.profilesManager = profilesManager;
        this.kifflarm = kifflarm;
        name = "";
        shortLabel = "";
        quick = false;
        iconId = Utils.getRandomNoteIconId();
        active = false;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        id = (int) date.getTime();

        folder = profilesManager.getProfilesFolder() + "/" + getId();

        alarmManager = new AlarmManager(kifflarm, getFolder());
        fileManager = new FileManager(kifflarm);
    }

    /** FILES **/
    public static final String PRF_ID_TAG = "id", PRF_NAME_TAG = "name", PRF_SHORT_TAG = "shortName",
            PRF_ACTIVE_TAG = "active", PRF_QUICK_TAG = "quick", PRF_ICON_ID_TAG = "icon_id",
            PRF_QUICK_INDEX = "quick_index", PRF_INDEX = "index";

    protected ArrayList<Param> getParams(){
        ArrayList<Param> params = new ArrayList<>();
        params.add(new Param(PRF_ID_TAG, Integer.toString(id)));
        params.add(new Param(PRF_NAME_TAG, name));
        params.add(new Param(PRF_SHORT_TAG, shortLabel));
        params.add(new Param(PRF_ACTIVE_TAG, Boolean.toString(active)));
        params.add(new Param(PRF_QUICK_TAG, Boolean.toString(quick)));
        params.add(new Param(PRF_ICON_ID_TAG, Integer.toString(iconId)));

        //profilesManager is null on creation for some reason
        if(profilesManager != null) {
            params.add(new Param(PRF_QUICK_INDEX, Integer.toString(profilesManager.getQuickIndex(this))));
            params.add(new Param(PRF_INDEX, Integer.toString(profilesManager.getProfileIndex(this))));
        }
        return params;
    }
    public void save(){
        fileManager.write(getParams(), getFolder(), kifflarm.getResources().getString(R.string.profile_prms), kifflarm.getResources().getString(R.string.profile_extension));
    }

    private void restoreParams(ArrayList<Param> params){
        for (Param p : params) {
            if (p.key.equals(PRF_NAME_TAG)) {
                name = p.value;
            } else if (p.key.equals(PRF_ID_TAG)) {
                id = Integer.parseInt(p.value);
            }  else if (p.key.equals(PRF_SHORT_TAG)) {
                shortLabel = p.value;
            } else if (p.key.equals(PRF_ACTIVE_TAG)) {
                active = Boolean.parseBoolean(p.value);
            } else if (p.key.equals(PRF_QUICK_TAG)) {
                quick = Boolean.parseBoolean(p.value);
            } else if (p.key.equals(PRF_ICON_ID_TAG)) {
                iconId = Integer.parseInt(p.value);
            } else if (p.key.equals(PRF_QUICK_INDEX)) {
                quickIndex = Integer.parseInt(p.value);
            } else if (p.key.equals(PRF_INDEX)) {
                index = Integer.parseInt(p.value);
            }
        }
    }

    public void deleteProfile(){
        alarmManager.activateAllAlarms(false);
        fileManager.delete(this);
    }

    public String getFullPath(){
        return folder;
    }

    public String getIdAsString(){
        return Integer.toString(id);
    }
    /** ALARMS **/
    public void removeAlarm(int index){
        alarmManager.removeAlarm(index);
    }

    public int getItemCount(){
        return alarmManager.getItemCount();
    }

    public void sortAlarms(){
        alarmManager.sortAlarms();
    }

    /** ACTIVATION **/
    public void activate(boolean activate){
        this.active = activate;
        alarmManager.activateAllAlarms(activate);
        save();
    }

    /** GET **/
    public Alarm getAlarm(int index){
        return alarmManager.getAlarm(index);
    }
    public boolean isActive() {
        return active;
    }
    public ArrayList<Alarm> getAlarms(){
        return alarmManager.getAlarms();
    }
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

    public int getIndex() {
        return index;
    }

    public int getQuickIndex() {
        return quickIndex;
    }

    /** SET **/
    public void setQuick(boolean quick) {
        this.quick = quick;
        save();
    }
    public void setQuickIndex(int i) {
        this.quickIndex = i;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
        save();
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
        save();
    }

    public void setName(String name) {
        this.name = name;
        save();
    }
}
