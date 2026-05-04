package com.kiefer.kifflarm.profiles;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.content.res.ResourcesCompat;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;

public class ProfileIconPopup extends Popup {
    public ProfileIconPopup(final KIFFLARM kifflarm, Profile profile, ImageView imageView){
        super(kifflarm);

        //inflate the View
        popupView = kifflarm.getLayoutInflater().inflate(R.layout.popup_palette, null);

        //create the popupWindow
        int width = FrameLayout.LayoutParams.WRAP_CONTENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        LinearLayout rowLayout = new LinearLayout(kifflarm);
        rowLayout.setOrientation(LinearLayout.VERTICAL);

        Integer[] icons = Utils.getIcons();

        int rows = 4, cols = 3;

        int i = 0;
        for(int row = 0; row < rows; row++){
            LinearLayout colLayout = new LinearLayout(kifflarm);
            colLayout.setOrientation(LinearLayout.HORIZONTAL);
            for(int col = 0; col < cols; col++){
                final ImageView iv = new ImageView(kifflarm);
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams((int)kifflarm.getResources().getDimension(R.dimen.quickIconWidth)*2, (int)kifflarm.getResources().getDimension(R.dimen.quickIconHeight)*2);
                flp.setMargins(0, 0, 0, 10);
                iv.setLayoutParams(flp);

                iv.setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), icons[i], null));
                colLayout.addView(iv);

                int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageView.setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), icons[finalI], null));
                        profile.setIconId(icons[finalI]);

                        popupWindow.dismiss();
                    }
                });

                i++;
            }
            rowLayout.addView(colLayout);
        }
        ((FrameLayout)popupView).addView(rowLayout);

        showAtLocation(popupWindow);
    }
}
