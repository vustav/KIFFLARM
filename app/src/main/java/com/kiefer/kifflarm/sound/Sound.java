package com.kiefer.kifflarm.sound;

import android.net.Uri;

public class Sound{
    public String name;
    Uri uri;

    public Sound(String name, Uri uri){
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }
}
