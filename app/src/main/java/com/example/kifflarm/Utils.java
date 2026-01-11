package com.example.kifflarm;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kifflarm.alarm.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Utils {

    public static void performHapticFeedback(View view){
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    public static void sortTimes(ArrayList<Alarm> alarms){
        Collections.sort(alarms, new Comparator<Alarm>() {
            public int compare(Alarm a1, Alarm a2) {
                return a1.compareTo(a2);
            }
        });
    }

    public static int insertInArraySorted(ArrayList<Alarm> alarms, Alarm newAlarm){
        for(int i = 0; i < alarms.size(); i++){
            Alarm oldAlarm = alarms.get(i);

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

    /** BG **/
    public static void setupBg(ViewGroup layout, TextView tv){
        layout.setBackground(Utils.getRandomGradientDrawable());

        String label = "ALARM";
        String concatLabel = "";

        int nOfCopys = 65;
        for(int copy = 0; copy <= nOfCopys; copy++){

            int start = 0;

            if(copy == 0){
                Random r = new Random();
                start = r.nextInt(label.length());
            }
            for(int i = start; i < label.length(); i++){
                concatLabel += String.valueOf(label.charAt(i));
            }
        }

        SpannableString coloredLabel = new SpannableString(concatLabel);
        for(int i = 0; i < coloredLabel.length() - 1; i++){
            coloredLabel.setSpan(new ForegroundColorSpan(Utils.getRandomColor()), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(coloredLabel);

        //add more if needed
        Random r = new Random();
        int startMargin = r.nextInt(40) + 30;
        int topMargin = r.nextInt(40) + 30;
        int endMargin = r.nextInt(80) + 30;

        //if(layout instanceof RelativeLayout) {
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlp.setMargins(-startMargin, -topMargin, -endMargin, 0);
            tv.setLayoutParams(rlp);
            /*
        }
        else if(layout instanceof LinearLayout) {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            llp.setMargins(-startMargin, -topMargin, -endMargin, 0);
            tv.setLayoutParams(llp);
        }

             */
    }
}
