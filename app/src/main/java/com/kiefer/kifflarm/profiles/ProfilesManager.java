package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.alarm.Alarmist;
import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;

import java.io.File;
import java.util.ArrayList;

public class ProfilesManager implements Alarmist {
    private final KIFFLARM kifflarm;
    private ArrayList<Profile> profiles;
    private final String profilesFolder;

    public ProfilesManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        profilesFolder = kifflarm.getResources().getString(R.string.profiles_folder);
        setupProfiles();
    }

    private void setupProfiles(){
        profiles = new ArrayList();
    }

    //ProfilesManager does not trigger load itself since it's done in onResume
    public void loadProfiles(FileManager fileManager){
        ArrayList<File> profileFolders = fileManager.getDirectoriesInPath(profilesFolder);

        for(File f : profileFolders){
            //get params with profile extension to get profile params, then restore
            ArrayList<ArrayList<Param>> paramsArray = fileManager.getParamsArrayFromFolder(profilesFolder + "/" + f.getName(), kifflarm.getResources().getString(R.string.profile_extension));

            if(!paramsArray.isEmpty()){
                for(ArrayList<Param> params : paramsArray){
                    profiles.add(new Profile(kifflarm, this, params));
                }
            }
        }

        activateProfile(1);
    }

    public void delete(int index){
        profiles.remove(index);
    }

    /** PROFILES **/
    public void addProfile(Profile profile){
        profiles.add(profile);
    }

    public void activateProfile(int index){
        for(Profile p : profiles){
            p.activate(false);
        }
        profiles.get(index).activate(true);
    }

    public Profile getActiveProfile(){
        for(Profile p : profiles) {
            if (p.isActive()) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Alarm> getActiveAlarms(){
            return getActiveProfile().getAlarms();
    }

    /** ALARMIST **/
    public Alarm getAlarm(int index){
        return getActiveProfile().getAlarm(index);
    }
    public ArrayList<Alarm> getAlarms(){
        return getActiveProfile().getAlarms();
    }
    public void removeAlarm(int index){
        getActiveProfile().removeAlarm(index);
    }
    public int getItemCount(){
        if(getActiveProfile() != null){
            return getActiveProfile().getItemCount();
        }
        else{
            return 0;
        }
    }
    public void sortAlarms(){
        getActiveProfile().sortAlarms();
    }

    /** GET **/
    public ArrayList<Profile> getProfiles() {
        return profiles;
    }

    public ArrayList<Profile> getQuickProfiles() {
        ArrayList<Profile> notesOnDesktop = new ArrayList<>();
        for (Profile p : profiles) {
            if (p.isQuick()) {
                notesOnDesktop.add(p);
            }
        }
        return notesOnDesktop;
    }

    public boolean getQuick(int index){
        return profiles.get(index).isQuick();
    }

    public String getProfilesFolder() {
        return profilesFolder;
    }

    /** SET **/
    public void setQuick(int index, boolean quick){
        profiles.get(index).setQuick(quick);
    }
}
