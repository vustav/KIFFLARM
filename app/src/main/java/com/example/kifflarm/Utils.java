package com.example.kifflarm;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.example.kifflarm.alarm.Alarm;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public static void sortTimes(ArrayList<Alarm> alarms){

    }

    public static int insertInArraySorted(ArrayList<Alarm> alarms, Alarm newAlarm){
        for(int i = 0; i < alarms.size(); i++){
            Alarm oldAlarm = alarms.get(i);

            /*
            if (newAlarm.getHour() <= oldAlarm.getHour()) {


                if(newAlarm.getHour() == oldAlarm.getHour()) {
                    if (newAlarm.getMinute() <= oldAlarm.getMinute()) {
                        alarms.add(i, newAlarm);
                        return i;
                    }
                }
                else{
                    alarms.add(i, newAlarm);
                    return i;
                }
            }

             */

            //if hour is smaller, insert
            if(newAlarm.getHour() < oldAlarm.getHour()){
                alarms.add(i, newAlarm);
                return i;
            }

            //if same, insert if minute is smaller
            else if(newAlarm.getHour() == oldAlarm.getHour()) {
                if (newAlarm.getMinute() <= oldAlarm.getMinute()) {
                    alarms.add(i, newAlarm);
                    return i;
                }
            }
        }

        //if not already added, add it las
        alarms.add(newAlarm);
        return alarms.size()-1;
    }

    /** COLORS **/
    public static int getRandomColor(){
        //Random random = new Random();
        //return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        return getRandomColorLight();
    }

    public static int getRandomColorLight(){
        Random random = new Random();
        int r = random.nextInt(256) / 2 + 256/2;
        int g = random.nextInt(256) / 2 + 256/2;
        int b = random.nextInt(256) / 2 + 256/2;
        return Color.rgb(r, g, b);
    }

    public static int getContrastColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        int newR = 255 - r;
        int newG = 255 - g;
        int newB = 255 - b;

        return Color.rgb(newR, newG, newB);
    }

    public static GradientDrawable getRandomGradientDrawable(){
        return getRandomGradientDrawable(getRandomColor(), getRandomColor());
    }

    public static GradientDrawable getRandomGradientDrawable(int colorOne, int colorTwo){
        Random random = new Random();
        //GradientDrawable gd;
        if(random.nextInt(2) == 1){
            //gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorOne, colorTwo});
            return getGradientDrawable(colorOne, colorTwo, VERTICAL);
        }
        else{
            //gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorOne, colorTwo});
            return getGradientDrawable(colorOne, colorTwo, HORIZONTAL);
        }
        //gd.setCornerRadius(0f);
        //return gd;
    }

    public static final int VERTICAL = 0, HORIZONTAL = 1;
    public static GradientDrawable getGradientDrawable(int colorOne, int colorTwo, int orientation){
        if(orientation == VERTICAL){
            return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorOne, colorTwo});
        }

        if(orientation == HORIZONTAL){
            return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorOne, colorTwo});
        }
        return null;
    }

    public static void randomizeTVLight(TextView tv){
        int color = getRandomColor();
        tv.setBackgroundColor(color);
        tv.setTextColor(getContrastColor(color));
    }

    /** IMG **/
    public static int getRandomImageId(){
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.drawable.bg_beach_small);
        list.add(R.drawable.bg_cat_small);
        list.add(R.drawable.bg_gtr_small);
        list.add(R.drawable.bg_ko_small);
        list.add(R.drawable.bg_rat_small);
        list.add(R.drawable.bg_dog_small);
        list.add(R.drawable.bg_tango_small);
        list.add(R.drawable.bg_back_small_cut);
        list.add(R.drawable.bg_wood);
        list.add(R.drawable.bg_matta);
        list.add(R.drawable.bg_boy);

        Random r = new Random();
        return list.get(r.nextInt(list.size()));
    }
}
