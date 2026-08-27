package com.kiefer.kifflarm.alarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.kiefer.kifflarm.alarm.receivers.AlarmReceiver2;
import com.kiefer.kifflarm.alarm.singles.KIFFMediaPlayer;
import com.kiefer.kifflarm.alarm.singles.KIFFVibrator;

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

    public static void alarmOff(Context context, Alarm alarm, Vibrator vibrator, MediaPlayer mediaPlayer, int startVolume){
        //alarm.activate(false);

        //turn it on again if it belongs to a profile. Day +1 is automatic
        if(!alarm.belongsToProfile()){
            alarm.activate(false);
        }
        else{
            alarm.activate(true);
        }
        //AlarmCannon.resetAlarmVolume(context, startVolume);
        //AlarmCannon.stopTimer();

        KIFFMediaPlayer.destroy();
        KIFFVibrator.destroy();
        AlarmReceiver2.stopTimer(context, startVolume);

        //vibrator.cancel();

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
