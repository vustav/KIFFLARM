package com.kiefer.kifflarm.profiles;

import android.util.Log;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.alarm.Alarm;
import com.kiefer.kifflarm.files.FileManager;
import com.kiefer.kifflarm.files.Param;
import com.kiefer.kifflarm.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class ProfilesManager {
    private KIFFLARM kifflarm;
    private ArrayList<Profile> profiles;
    private String profilesFolder;

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



        //recreate saved alarms if there are any
        //ArrayList<ArrayList<Param>> paramsArray = kifflarm.getFileManager().getParamsArray();


    }

    public void delete(int index){
        profiles.remove(index);
    }

    /** PROFILES **/
    public void addProfile(Profile profile){
        profiles.add(profile);
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
