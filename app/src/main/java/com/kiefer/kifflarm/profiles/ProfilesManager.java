package com.kiefer.kifflarm.profiles;

import java.util.ArrayList;

public class ProfilesManager {
    private ArrayList<Profile> profiles;

    public ProfilesManager(){
        setupProfiles();
    }

    private void setupProfiles(){
        profiles = new ArrayList();

        for(int i = 0; i<5; i++){
            profiles.add(new Profile());
        }
    }

    public void delete(int index){
        profiles.remove(index);
    }

    public void setQuick(int index, boolean quick){
        profiles.get(index).setQuick(quick);
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
}
