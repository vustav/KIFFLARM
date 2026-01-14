package com.example.kifflarm.alarm;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class AlarmUtils {

    public static void startVibrating(Vibrator vibrator) {
        //exempel: https://developer.android.com/develop/ui/views/haptics/custom-haptic-effects#java_1

        if (Build.VERSION.SDK_INT >= 26) {
            long[] timings = new long[] { 50, 50, 100, 50, 50 };
            int[] amplitudes = new int[] { 64, 128, 255, 128, 64 };
            int repeat = 1; // Repeat from the second entry, index = 1.
            VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
            // repeatingEffect can be used in multiple places.

            vibrator.vibrate(repeatingEffect);
        } else {
            //deprecated in API 26
            long[] pattern = {500};
            vibrator.vibrate(pattern, 0);
        }
    }

    public static void playRingtone(MediaPlayer mediaPlayer){
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("AlarmUtils ZZZ", "playRingTone, " + e);
        }
    }

    public static void alarmOff(Alarm alarm, Vibrator vibrator, MediaPlayer mediaPlayer){
        alarm.setActive(false);
        vibrator.cancel();
        mediaPlayer.stop();
    }
}
