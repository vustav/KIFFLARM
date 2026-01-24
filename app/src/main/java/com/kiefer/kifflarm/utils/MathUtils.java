package com.kiefer.kifflarm.utils;

import com.kiefer.kifflarm.views.ClockView;

public class MathUtils {
    public static final int MAX_SNOOZE = 30;

    public static int convertAngleToHour(float angle){
        float angleProc = angle / 360f;
        return (int) (angleProc * 12f);
    }
    public static float convertHourToAngle(int time){
        return (time * 360f) / 12f;
    }
    public static int convertAngleToMinute(float angle){
        float angleProc = angle / 360f;
        return (int) (angleProc * 60f);
    }
    public static int convertAngleToSnooze(float angle){
        float angleProc = angle / 360f;
        return (int) (angleProc * (float) MAX_SNOOZE);
    }
    public static float convertMinuteToAngle(int time){
        return (time * 360f) / 60f;
    }
    public static float convertSnoozeToAngle(int time){
        return (time * 360f) / (float) MAX_SNOOZE;
    }

    public static float turnAntiClock(float oldDeg, int degreesToTurn){
        float deg;
        if(oldDeg > 360 - degreesToTurn) {
            deg = oldDeg + degreesToTurn - 360;
        }
        else{
            deg = oldDeg + degreesToTurn;
        }
        return deg;
    }

    public static float turnClockwise(float oldDeg, int degreesToTurn){
        float deg;
        if(oldDeg > 360 - degreesToTurn) {
            deg = oldDeg - degreesToTurn;
        }
        else{
            deg = oldDeg - degreesToTurn;
        }
        return deg;
    }
}
