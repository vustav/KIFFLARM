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

import com.kiefer.kifflarm.R;
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
            //sometimes crashes. mYBE when alarms with the same time as the first are added.
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

    public static String shortenString(String title, int thresh){
        //int thresh = 10;
        if(title.length() > thresh) {
            return title.substring(0, thresh) + "...";
        }
        return title;
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

    /** ICON **/

    public static int getRandomNoteIconId(){
        Random r = new Random();
        ArrayList<Integer> list = getIcons();
        return list.get(r.nextInt(list.size()));
    }

    //only 12 in the popup
    public static ArrayList<Integer> getIcons(){
        ArrayList<Integer> list = new ArrayList<>();

        //return new ArrayList<>()
                /*
                R.drawable.icon_note0,
                R.drawable.icon_note1,
                R.drawable.icon_note5,
                R.drawable.icon_note7,
                //R.drawable.icon_note8,
                R.drawable.icon_note10,
                R.drawable.icon_note11,
                R.drawable.icon_note12,
                R.drawable.icon_note13,
                R.drawable.icon_note14,
                R.drawable.icon_note16,
                R.drawable.icon_note17,
                //R.drawable.icon_note18,
                R.drawable.icon_note19,

                R.drawable.icon_face_angry,
                R.drawable.icon_face_devilish,
                R.drawable.icon_face_embarrassed,
                R.drawable.icon_face_heart,
                R.drawable.icon_face_heart_broken,
                R.drawable.icon_face_laugh,
                R.drawable.icon_face_sick,
                R.drawable.icon_face_smirk,
                R.drawable.icon_face_surprise,
                R.drawable.icon_face_tired,
                R.drawable.icon_face_uncertain,
                R.drawable.icon_face_worried,

                R.drawable.icon_stock_smiley0,
                R.drawable.icon_stock_smiley1,
                R.drawable.icon_stock_smiley11,
                R.drawable.icon_stock_smiley13,
                R.drawable.icon_stock_smiley15,
                R.drawable.icon_stock_smiley18,
                R.drawable.icon_search2,
                R.drawable.icon_stock_smiley22,
                R.drawable.icon_stock_smiley3,
                R.drawable.icon_stock_smiley4,
                R.drawable.icon_stock_smiley5,
                R.drawable.icon_stock_smiley6,
                R.drawable.icon_stock_smiley7,
                R.drawable.icon_stock_smiley8,

                 */

        //NEW

        list.add(R.drawable.icon_cucumber100);
        list.add(R.drawable.icon_green100);
        list.add(R.drawable.icon_man100);
        //list.add(R.drawable.icon_smile100);
        list.add(R.drawable.icon_walk100);
        list.add(R.drawable.icon_bajs100);
        list.add(R.drawable.icon_orient100);

        list.add(R.drawable.icon_pommes100);
        list.add(R.drawable.icon_bagrock100);
        list.add(R.drawable.icon_cold100);
        list.add(R.drawable.icon_bomb100);
        list.add(R.drawable.icon_chips100);
        //list.add(R.drawable.icon_sandwich100);

        list.add(R.drawable.icon_bust100);
        list.add(R.drawable.icon_dog100);
        list.add(R.drawable.icon_drink_limon100);
        list.add(R.drawable.icon_girl100);
        list.add(R.drawable.icon_girl2100);
        list.add(R.drawable.icon_girlhat100);
        list.add(R.drawable.icon_glass_berri100);
        //list.add(R.drawable.icon_grodagg100);
        //list.add(R.drawable.icon_grodpump100);
        //list.add(R.drawable.icon_happystars100);
        list.add(R.drawable.icon_iceman100);
        list.add(R.drawable.icon_jorden100);
        list.add(R.drawable.icon_kokosdrink100);
        list.add(R.drawable.icon_ladyface100);
        //list.add(R.drawable.icon_lilamonster100);
        list.add(R.drawable.icon_oopsface100);
        list.add(R.drawable.icon_peanutman100);
        //list.add(R.drawable.icon_slemmonster100);
        list.add(R.drawable.icon_solstol100);
        list.add(R.drawable.icon_sunface100);
        //list.add(R.drawable.icon_tv100);
        list.add(R.drawable.icon_walkman100);
        //list.add(R.drawable.icon_toa100);
        list.add(R.drawable.icon_skola100);
        list.add(R.drawable.icon_padda100);
        list.add(R.drawable.icon_lek100);
        list.add(R.drawable.icon_glass_uon100);

        list.add(R.drawable.icon_bar100);
        list.add(R.drawable.icon_cd100);
        list.add(R.drawable.icon_dator100);
        list.add(R.drawable.icon_dinorex100);
        list.add(R.drawable.icon_dubbelmonst100);
        list.add(R.drawable.icon_garage100);
        list.add(R.drawable.icon_garnboll100);
        list.add(R.drawable.icon_girlrut100);
        list.add(R.drawable.icon_godishalsband100);
        list.add(R.drawable.icon_haj100);
        list.add(R.drawable.icon_kistor100);
        list.add(R.drawable.icon_lamps100);
        list.add(R.drawable.icon_manwave100);
        list.add(R.drawable.icon_maskin100);
        //list.add(R.drawable.icon_pinkmonst100);
        list.add(R.drawable.icon_pizza100);
        //list.add(R.drawable.icon_ritaddog100);
        list.add(R.drawable.icon_sax100);
        list.add(R.drawable.icon_stad100);
        list.add(R.drawable.icon_stereo100);
        list.add(R.drawable.icon_streetlight100);
        list.add(R.drawable.icon_telef100);
        list.add(R.drawable.icon_video100);
        //list.add(R.drawable.icon_worm100);
        list.add(R.drawable.icon_worm2100);

        list.add(R.drawable.icon_v100);

        list.add(R.drawable.icon_bigleg100);
        list.add(R.drawable.icon_emojicool100);
        list.add(R.drawable.icon_emojiglasses100);
        list.add(R.drawable.icon_emojihearteyes100);
        list.add(R.drawable.icon_emojiooooooo100);
        list.add(R.drawable.icon_emojisad100);
        list.add(R.drawable.icon_emojitongue100);
        list.add(R.drawable.icon_emojizzzzzzzz100);
        list.add(R.drawable.icon_family100);
        list.add(R.drawable.icon_girlglasses100);
        list.add(R.drawable.icon_mirc100);
        list.add(R.drawable.icon_muscles100);
        list.add(R.drawable.icon_paper100);
        list.add(R.drawable.icon_emojibasic100);

        list.add(R.drawable.icon_workout100);
        list.add(R.drawable.icon_cam100);

        return list;
        //};
    }

    /** COMPARE **/

}
