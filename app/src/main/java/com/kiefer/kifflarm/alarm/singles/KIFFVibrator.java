package com.kiefer.kifflarm.alarm.singles;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class KIFFVibrator {
    //used to get access to the same object when starting and ending the alarm
    private static Vibrator vibrator;

    private KIFFVibrator() {
        //
    }

    public static Vibrator getInstance(Context context) {
        if(vibrator == null) {
            vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        }
        return vibrator;
    }

    public static void destroy() {
        Log.e("KIFFVibrator ZZZ", "destroy");
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
    }
}