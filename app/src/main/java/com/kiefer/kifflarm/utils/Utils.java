package com.kiefer.kifflarm.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiefer.kifflarm.alarm.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Utils {

    /** VIBB **/
    public static void performHapticFeedback(View view){
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    /** SORTING **/
    public static void sortAlarms(ArrayList<Alarm> alarms){
        Collections.sort(alarms, getComparator());
    }

    public static int insertAlarm(ArrayList<Alarm> alarms, Alarm newAlarm){
        try {
            int position = Math.abs(Collections.binarySearch(alarms, newAlarm, getComparator())) - 1;
            alarms.add(position, newAlarm);
            return position;
        }
        catch (Exception e){
            //sometimes crashes. I Think it is when alarms with the same time as the first are added.
            Log.e("Utils ZZZ", "insertAlarm");
            alarms.add(newAlarm);
            sortAlarms(alarms);
            return -1;
        }
    }

    private static Comparator<Alarm> getComparator(){
        return new Comparator<Alarm>() {
            public int compare(Alarm a1, Alarm a2) {
                return a1.compareTo(a2);
            }
        };
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

    /** GRADIENT DRAWABLES **/
    public static final int VERTICAL = 0, HORIZONTAL = 1;
    public static GradientDrawable getRandomGradientDrawable(){
        return getRandomGradientDrawable(getRandomColor(), getRandomColor());
    }

    public static GradientDrawable getRandomGradientDrawable(int colorOne, int colorTwo){
        Random random = new Random();
        if(random.nextInt(2) == 1){
            return getGradientDrawable(colorOne, colorTwo, VERTICAL);
        }
        else{
            return getGradientDrawable(colorOne, colorTwo, HORIZONTAL);
        }
    }

    public static GradientDrawable getGradientDrawable(int colorOne, int colorTwo, int orientation){
        if(orientation == VERTICAL){
            return new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{colorOne, colorTwo});
        }

        if(orientation == HORIZONTAL){
            return new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorOne, colorTwo});
        }
        return null;
    }

    /** BG **/
    public static void createNiceBg(ViewGroup layout, TextView tv, int nOfCopys){
        layout.setBackground(getRandomGradientDrawable());

        String label = "ALARM";
        String concatLabel = "";
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

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlp.setMargins(-startMargin, -topMargin, -endMargin, 0);
        tv.setLayoutParams(rlp);
    }
    public static void createNiceBg(ViewGroup layout, TextView tv, int nOfCopys, int height){
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = layout.getWidth();
                int height = layout.getHeight();


                Log.e("Utils ZZZ", "layout h: "+height);
                layout.setBackground(getRandomGradientDrawable());



                tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        String label = "ALARM";
                        String concatLabel = "";

                        for(int copy = 0; copy <= nOfCopys; copy++){

                            int start = 0;

                            if(copy == 0){
                                Random r = new Random();
                                start = r.nextInt(label.length());
                            }
                            for(int i = start; i < label.length(); i++){
                                concatLabel += String.valueOf(label.charAt(i));
                            }
                            tv.setText(concatLabel);
                            Log.e("Utils ZZZ", "tv h: "+tv.getHeight());
                        }
                    }
                });

                SpannableString coloredLabel = new SpannableString(tv.getText());
                for(int i = 0; i < coloredLabel.length() - 1; i++){
                    coloredLabel.setSpan(new ForegroundColorSpan(Utils.getRandomColor()), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tv.setText(coloredLabel);

                //add more if needed
                Random r = new Random();
                int startMargin = r.nextInt(40) + 30;
                int topMargin = r.nextInt(40) + 30;
                int endMargin = r.nextInt(80) + 30;

                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.setMargins(-startMargin, -topMargin, -endMargin, 0);
                tv.setLayoutParams(rlp);
                Log.e("Utils ZZZ", "tv h no loop: "+tv.getHeight());
            }
        });
    }

    /** STRING **/
    public static String timeToString(int time){
        //add 0 to the start if below 10
        String timeString = Integer.toString(time);
        if(time < 10){
            timeString = "0"+time;
        }
        return timeString;
    }

    /** NMBRS **/
    public static int getRandomOffset(int max){
        Random r  = new Random();
        return max - r.nextInt(max+1) - r.nextInt(max+1);
    }
    public static int getRandomOffset(){
        return getRandomOffset(5);
    }

    public static int getRandomPositiveOffset(int min, int max){
        Random r  = new Random();
        return r.nextInt(max-min+1)+min;
    }
}
