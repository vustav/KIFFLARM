package com.kiefer.kifflarm.profiles;

import com.kiefer.kifflarm.utils.Utils;

import java.util.Random;

public class Profile {
    private String name;
    private boolean quick;
    private int iconId;

    public Profile(){
        Random r = new Random();
        name = Integer.toString(r.nextInt());
        quick = false;
        iconId = Utils.getRandomNoteIconId();
    }

    /** GET **/
    public String getName() {
        return name;
    }
    public String getShort(){
        return name.substring(0, 2);
    }
    public boolean isQuick() {
        return quick;
    }

    public int getIconId() {
        return iconId;
    }

    /** SET **/
    public void setQuick(boolean quick) {
        this.quick = quick;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
