package com.example.kifflarm.sound;

import android.net.Uri;

import java.util.ArrayList;

public class Sounds {
    ArrayList<Sound> sounds;

    public Sounds(){
        sounds = new ArrayList<>();
    }

    public void addSound(String name, Uri uri){
        //String[] sound = {name, uri};
        sounds.add(new Sound(name, uri));
    }

    public Sound get(int i){
        return sounds.get(i);
    }

    public int size(){
        return sounds.size();
    }
}
