package com.example.kifflarm.popups;

import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;
import com.example.kifflarm.Utils;
import com.example.kifflarm.sound.Sound;
import com.example.kifflarm.sound.SoundManager;

public class SoundPopup extends Popup {
    private SoundManager soundManager;
    private Sound notPlaying = new Sound("not playing", null); // just a dummy
    private Sound currentlyPreviewedSound = notPlaying;
    private MediaPlayer mediaPlayer;

    public SoundPopup(KIFFLARM kifflarm, AlarmSettingsPopup alarmSettingsPopup, Alarm alarm){
        super(kifflarm);

        soundManager = kifflarm.getSoundManager();

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_sound, null);
        popupView.setBackground(Utils.getRandomGradientDrawable());

        //create the popupWindow
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set a margin on the window since we want to max its size
        Display display = kifflarm.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        popupWindow.setWidth(size.x-size.x/5);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the recyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.popupSoundsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        SoundPopupAdapter soundPopupAdapter = new SoundPopupAdapter(recyclerView, soundManager, alarmSettingsPopup, this, alarm);
        recyclerView.setAdapter(soundPopupAdapter);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(mediaPlayer != null) {
                    mediaPlayer.stop(); //throws IllegalStateException if not already stopped
                    mediaPlayer.release();
                }
            }
        });

        showAtLocation(popupWindow);
    }

    /** the only way I could get this to work was to create a MediaPlayer on playPreview() and
     * delete/release it in stopPreview(). Should probably fix this at some point... **/
    private void setupMediaPlayer(){
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        //.setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        //.setLegacyStreamType(AudioManager.STREAM_ALARM)
                        //.setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        stopPreview();
                    }
                });
            }
        } catch(Exception e){
            Log.e("AlarmActivity ZZZ", "setupMediaPlayer" + e);
        }
    }

    /** PREVIEW **/
    //MÅSTE FIXA NÅN medaPlayer-lyssnare som kan null currently när den är slut

    public void playPreview(Sound sound){

        if(isPreviewPlaying()){
            stopPreview();
        }

        setupMediaPlayer();

        currentlyPreviewedSound = sound;
        Log.e("SoundManager ZZZ", "play: "+sound.getName());
        try {
            mediaPlayer.setDataSource(kifflarm.getApplicationContext(), sound.getUri());
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IllegalStateException ise){
            ise.printStackTrace();
        }catch (Exception e) {
            Log.e("AlarmActivity ZZZ", "playPreview, " + e);
        }
    }

    public void stopPreview(){
        currentlyPreviewedSound = notPlaying;
        Log.e("SoundManager ZZZ", "stop");
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean isPreviewPlaying() {
        return currentlyPreviewedSound != notPlaying;
    }

    public Sound getCurrentlyPreviewedSound(){
        return currentlyPreviewedSound;
    }
}

