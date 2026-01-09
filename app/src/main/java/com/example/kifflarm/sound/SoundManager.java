package com.example.kifflarm.sound;

import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.kifflarm.KIFFLARM;

import java.util.Random;

public class SoundManager {
    private final KIFFLARM kifflarm;
    private Sounds sounds;

    public SoundManager(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
        setupSounds();
    }

    public void setupSounds() {
        RingtoneManager manager = new RingtoneManager(kifflarm);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();

        sounds = new Sounds();
        while (cursor.moveToNext()) {
            String name = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri uri = Uri.parse(cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX));

            sounds.addSound(name, uri);

        }
    }

    /** GET **/
    public Sounds getSounds() {
        return sounds;
    }

    public Sound getRandomSound(){
        Random r = new Random();
        return sounds.get(r.nextInt(sounds.size()));
    }
}
