package com.kiefer.kifflarm.alarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;

import com.kiefer.kifflarm.alarm.receivers.AlarmReceiver2;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

import java.util.ArrayList;
import java.util.List;

public class AlarmUtils {

    public static void startVibrating(Context context) {

        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            // Get the manager
            VibratorManager manager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);

            // Get the vibrator
            vibrator = manager.getDefaultVibrator();
        }
        // Use the old API
        else
        {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }





        //exempel: https://developer.android.com/develop/ui/views/haptics/custom-haptic-effects#java_1
        //final Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        vibrator.cancel();

        /*
        if (Build.VERSION.SDK_INT >= 26) {
            Log.e("AlarmUtils ZZZ", "vibrator > 26");

            long[] timings = new long[] { 0, 50, 50, 50, 50 , 50, 50};
            int[] amplitudes = new int[] {255, 255, 255, 255, 255, 255, 255};
            int repeat = 1; // Repeat from the second entry, index = 1.
            VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat);
            //VibrationEffect repeatingEffect = VibrationEffect.createWaveform(timings, repeat);
            // repeatingEffect can be used in multiple places.

            vibrator.vibrate(repeatingEffect);
        } else {
            Log.e("AlarmUtils ZZZ", "vibrator < 26");
            //deprecated in API 26
            long[] pattern = {500};
            vibrator.vibrate(pattern, 0);
        }

         */

        // API 26+
        long[] timings = new long[] { 150, 150, 150, 150, 150, 150, 150};
        int[] amplitudes = new int[] {255, 255, 255, 255, 255, 255, 255};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // Match amplitudes to the timings list (0 for pause, DEFAULT_AMPLITUDE for vibrate)
            /*
            List<Integer> amplitudes = new ArrayList<>();
            int repeatPattern = timings.length / 3;

            // Timings list is built around a pause, vibrate, pause sequence, so build the
            // amplitude list the same way
            for (int i = 0; i < repeatPattern; i++)
            {
                amplitudes.add(0);
                amplitudes.add(VibrationEffect.DEFAULT_AMPLITUDE);
                amplitudes.add(0);
            }

            // This vibration sequence uses a pattern that ends with a different wait than
            // wait in between vibrations. Add another wait at the end to account for this pattern
            if (amplitudes.size() != timings.length)
            {
                amplitudes.add(0);
            }

             */



            // Create a vibration that will repeat indefinitely (that is what the 0 is for)
            VibrationEffect effect = VibrationEffect.createWaveform(timings, amplitudes, 0);

            // Vibrate (API 33+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {
                VibrationAttributes attr = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM);
                vibrator.vibrate(effect, attr);
            }
            // Vibrate (API 26-32)
            else
            {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                vibrator.vibrate(effect, attr);
            }
        }
        // API 25-
        else
        {
            // Vibrate
            vibrator.vibrate(timings, 0);
        }
    }

    public static void cancelVibration(Context context){
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).cancel();
    }

    public static void playRingtone(MediaPlayer mediaPlayer){
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("AlarmUtils ZZZ", "playRingTone, " + e);
        }
    }

    public static void alarmOff(Context context, Alarm alarm, MediaPlayer mediaPlayer, int startVolume){
        //alarm.activate(false);

        //turn it on again if it belongs to a profile. Day +1 is automatic
        if(!alarm.belongsToProfile()){
            alarm.activate(false);
        }
        else{
            alarm.activate(true);
        }

        KIFFMediaPlayer.destroy();
        //KIFFVibrator.destroy();
        AlarmReceiver2.stopTimer(context, startVolume);

        //vibrator.cancel();
        cancelVibration(context);

        if(alarm.isSnooze()){
            alarm.deleteAlarm();
        }

        try {
            mediaPlayer.stop();
        }
        catch (IllegalStateException ise){
            Log.e("AlarmUtils ZZZ", "alarmOff, "+ise);
        }
    }

    public static void setSnooze(Context context, Alarm alarm){
        Alarm newAlarm = new Alarm(context, alarm.getSound(), alarm.getFolder(), false);
        newAlarm.setIsSnooze(true);
        newAlarm.setTime(alarm.getHour(), alarm.getMinute() + alarm.getSnoozeTime());
        newAlarm.activate(true);
    }
}
