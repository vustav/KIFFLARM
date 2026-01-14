package com.example.kifflarm.alarm.singles;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class KIFFMediaPlayer {
    //used to get access to the same object when starting and ending the alarm
    private static MediaPlayer mediaPlayer;

    private KIFFMediaPlayer(){
        //
    }

    public static MediaPlayer getInstance(Context context, Uri sound){
        if(mediaPlayer == null){
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                mediaPlayer.setDataSource(context.getApplicationContext(), sound);
                mediaPlayer.setLooping(true);
            } catch (Exception e) {
                Log.e("AlarmActivity ZZZ", "setupMediaPlayer"+e);
            }
        }
        return mediaPlayer;
    }

    public static void destroy(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
