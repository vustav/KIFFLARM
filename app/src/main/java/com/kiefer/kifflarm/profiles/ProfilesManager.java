package com.kiefer.kifflarm.profiles;

import java.util.ArrayList;

public class ProfilesManager {
    private ArrayList profiles;

    public ProfilesManager(){
        setupProfiles();
    }

    private void setupProfiles(){
        profiles = new ArrayList();
    }

    /** GET **/
    public ArrayList getProfiles() {
        return profiles;
    }
}
