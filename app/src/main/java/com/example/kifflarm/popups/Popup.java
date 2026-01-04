package com.example.kifflarm.popups;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.example.kifflarm.KIFFLARM;
import com.example.kifflarm.R;

import java.util.Random;

public abstract class Popup {
    protected KIFFLARM kifflarm;
    protected PopupWindow popupWindow;
    protected View popupView;
    public Popup(KIFFLARM kifflarm){
        this.kifflarm = kifflarm;
    }

    /** SHOW **/
    public void showAtLocation(PopupWindow popupWindow){
        showAtLocation(popupWindow, Gravity.CENTER, .8f, true);
    }

    protected void showAtLocation(PopupWindow popupWindow, int gravity, float dimAmount, boolean offset){

        int modX, modY;
        if(offset) {
            Random r = new Random();
            int off = kifflarm.getResources().getInteger(R.integer.popupOffset);
            modX = (off / 2) - r.nextInt(off);
            modY = (off / 2) - r.nextInt(off);
        }
        else{
            modX = 0;
            modY = 0;
        }

        popupWindow.showAtLocation(kifflarm.getLayout(), gravity, modX, modY);
        dimBehind(popupWindow, dimAmount);
    }

    /** DIM **/
    public static void dimBehind(PopupWindow popupWindow, float dimAmount) {
        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = dimAmount;
        wm.updateViewLayout(container, p);
    }

    public void dismiss(){
        if(popupWindow != null){
            popupWindow.dismiss();
        }
    }
}

