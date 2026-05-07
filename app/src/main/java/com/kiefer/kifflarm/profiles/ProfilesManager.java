package com.kiefer.kifflarm.profiles;

import android.util.Log;

import com.kiefer.kifflarm.alarm.Alarmist;
import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class ProfilesManager implements Alarmist {
    private final KIFFLARM kifflarm;
    private ArrayList<Profile> profiles;
    private final String profilesFolder;

    public ProfilesManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        profilesFolder = kifflarm.getResources().getString(R.string.profiles_folder);
        profiles = new ArrayList();
    }

    //ProfilesManager does not trigger load itself since it's done in onResume
    public void loadProfiles(FileManager fileManager){
        profiles = new ArrayList<>();
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
        //for(Profile p : profiles){
        //    Log.e("ProfilesManager ZZZ", p.getName()+", "+p.getIndex());
        //}
        //Log.e("ProfilesManager ZZZ", "------------------");
        sortProfiles();
        //for(Profile p : profiles){
        //    Log.e("ProfilesManager ZZZ", p.getName()+", "+p.getIndex());
        //}

        //activateProfile(1);
        Profile activeProfile = getActiveProfile();
        if(activeProfile != null){
            activateProfile(activeProfile, true);
        }

        //since profiles are loaded in onResume we need to do this update here, in KIFFLARM.setupLayout
        //no profiles exist yet
        kifflarm.updateProfilesUI();
    }

    public void delete(int index){
        profiles.remove(index).deleteProfile();
    }

    /** PROFILES **/

    public void moveProfile(int from, int to){
        Profile p = profiles.remove(from);
        profiles.add(to, p);
        //Log.e("ZZZ", "move");

        //kanske save to from räcker?
        saveProfiles();
    }

    /*
    QuickProfiles is never saved, getQuickProfiles loops through all profiles and returns a new array
    every time it's called, so we can't just move profiles in an array. It's not saved anywhere, and
    that's why we do it like this.
     */
    public void moveQuickProfile(int from, int to){
        ArrayList<Profile> quickProfiles = getQuickProfiles();
        Profile p = quickProfiles.remove(from);
        quickProfiles.add(to, p);
        for(int i = 0; i<quickProfiles.size(); i++){
            quickProfiles.get(i).setQuickIndex(i);
        }
        saveProfiles();
    }
    public void saveProfiles(){
        for(Profile p : profiles){
            p.save();
        }
    }
    public void addProfile(Profile profile){
        profiles.add(profile);
    }

    public void activateProfile(int index, boolean activate){
        activateProfile(profiles.get(index), activate);
    }

    public void activateProfile(Profile profile, boolean activate){
        deactivateAllProfiles();
        if(activate) {
            profile.activate(true);
        }
        //kifflarm.updateProfilesUI();
    }

    public void deactivateAllProfiles(){
        for(Profile p : profiles){
            p.activate(false);
        }
    }

    public void activateQuickProfile(int index){
        Profile profile = getQuickProfiles().get(index);
        activateProfile(profile, !profile.isActive());

        //kifflarm.getQuickProfilesAdapter().notifyDataSetChanged();
    }

    public Profile getActiveProfile(){
        for(Profile p : profiles) {
            if (p.isActive()) {
                return p;
            }
        }
        return null;
    }

    /*
   QuickProfiles is never saved, getQuickProfiles loops through all profiles and returns a new array
   every time it's called. The order is always the same as in profiles array. QuickIndexes are saved
   in moveQuickProfile, and here we sort before returning.
    */
    public ArrayList<Profile> getQuickProfiles(){
        ArrayList<Profile> quickProfiles = new ArrayList<>();
        for(Profile p : profiles){
            if(p.isQuick()){
                quickProfiles.add(p);
            }
        }
        sortQuickProfiles(quickProfiles);
        return quickProfiles;
    }

    public ArrayList<Alarm> getActiveAlarms(){
        return getActiveProfile().getAlarms();
    }

    public int getProfileIndex(Profile p){
        return profiles.indexOf(p);
    }

    public int getQuickIndex(Profile p){
        return getQuickProfiles().indexOf(p);
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

    public boolean isQuick(int index){
        return profiles.get(index).isQuick();
    }

    public String getProfilesFolder() {
        return profilesFolder;
    }

    /** SET **/
    public void setQuick(int index, boolean quick){
        profiles.get(index).setQuick(quick);
    }

    /** SORT **/
    //index is saved and sort is only done on loading. For quick it's done every getQuickProfiles is called
    // since they're not stored anywhere
    private void sortProfiles(){
        profiles.sort(getComparatorIndex());
    }
    private Comparator<Profile> getComparatorIndex(){
        return new Comparator<Profile>() {
            public int compare(Profile n1, Profile n2) {
                int index1 = n1.getIndex();
                int index2 = n2.getIndex();
                return index1- index2;
            }
        };
    }

    //profiles in argument here since we'd get an infinite loop if we used getQuickProfiles
    private void sortQuickProfiles(ArrayList<Profile> profiles){
        profiles.sort(getComparatorQuickIndex());
    }
    public static Comparator<Profile> getComparatorQuickIndex(){
        return new Comparator<Profile>() {
            public int compare(Profile n1, Profile n2) {
                int quick1 = n1.getQuickIndex();
                int quick2 = n2.getQuickIndex();
                return quick1 - quick2;
            }
        };
    }
}
