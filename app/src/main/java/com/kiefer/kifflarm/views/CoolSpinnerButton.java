package com.kiefer.kifflarm.views;

//just the button. Open a ListPopup in the listener.

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.R;

public class CoolSpinnerButton extends RelativeLayout {
    protected final Button button;

    public CoolSpinnerButton(Activity activity){
        super(activity);

        LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(rlp);

        button = new Button(activity);
        button.setId(View.generateViewId());
        rlp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        button.setLayoutParams(rlp);
        button.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.custom_btn, null));
        button.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.defaultBtnTxtColor, null));
        //button.setTextSize(TypedValue.COMPLEX_UNIT_PX, TextUtils.getDefaultTextSize(activity, ));
        //button.setPadding(5, 0, 0, 0);
        button.setSingleLine();

        addView(button);
    }

    /** sET **/

    public void setWidth(int width){
        button.getLayoutParams().width = width;
    }

    public void removePadding(){
        button.setPadding(0, 0, 10, 0);
    }

    public void setPadding(int l, int t, int r, int b){
        button.setPadding(l, t, 10, b);
    }

    public void setPadding(int padding){
        button.setPadding(padding, padding, padding, 0);
    }

    public void setHeight(int height){
        button.getLayoutParams().height = height;
    }

    public void setSelection(String s){
        String string = Utils.shortenString(s, 7)+" ▼";
        button.setText(string);
    }

    public void setTextSize(float size){
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public TextView getTextView(){
        return button;
    }

    public void setButtonWidth(int width){
        button.setWidth(width);
    }

    public void setButtonHeight(int height){
        button.setHeight(height);
    }

    public void setTypeface(Typeface tf){
        button.setTypeface(tf);
    }

    public void setOnClickListener(OnClickListener listener){
        button.setOnClickListener(listener);
    }

    @Override
    public void setAlpha(final float alpha){
        button.setAlpha(alpha);
    }

    @Override
    public void setEnabled(final boolean enabled){
        button.setEnabled(enabled);
    }
}
