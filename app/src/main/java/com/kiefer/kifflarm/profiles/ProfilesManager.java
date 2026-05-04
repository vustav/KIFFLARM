package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.KIFFLARM;

import java.util.ArrayList;

public class ProfilesManager {
    private KIFFLARM kifflarm;
    private ArrayList<Profile> profiles;

    public ProfilesManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        setupProfiles();
    }

    private void setupProfiles(){
        profiles = new ArrayList();

        for(int i = 0; i<5; i++){
            profiles.add(new Profile(kifflarm));
        }
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

    /** SET **/
    public void setQuick(int index, boolean quick){
        profiles.get(index).setQuick(quick);
    }
}
