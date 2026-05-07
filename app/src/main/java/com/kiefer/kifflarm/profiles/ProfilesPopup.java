package com.kiefer.kifflarm.profiles;

import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.drawables.DrawablePlus;
import com.kiefer.kifflarm.popups.Popup;
import com.kiefer.kifflarm.utils.Utils;

public class ProfilesPopup extends Popup {
    private ProfilesPopupAdapter profilesPopupAdapter;
    //private ProfilesManager profilesManager;
    public ProfilesPopup(KIFFLARM kifflarm, ProfilesManager profilesManager){
        super(kifflarm);

        //this.profilesManager = profilesManager;

        //inflate the View
        popupView = this.kifflarm.getLayoutInflater().inflate(R.layout.popup_profiles, null);

        //create the popupWindow
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //set a margin on the window since we want to max its size
        Display display = kifflarm.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        popupWindow.setWidth(size.x-size.x/5);
        popupWindow.setHeight(size.y - size.y/4);

        //bg
        RelativeLayout bg = popupView.findViewById(R.id.profilesPopupBg);
        TextView bgTv = popupView.findViewById(R.id.profilesPopupBgTV);
        Utils.createNiceBg(bg, bgTv, 70);

        //add a nice animation
        popupWindow.setAnimationStyle(R.style.popup_animation);

        //set up the recyclerView
        RecyclerView recyclerView = popupView.findViewById(R.id.profilesPopupRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(kifflarm));

        profilesPopupAdapter = new ProfilesPopupAdapter(kifflarm, this, recyclerView, profilesManager);
        recyclerView.setAdapter(profilesPopupAdapter);

        ProfilesTouchHelper touchHelper = new ProfilesTouchHelper(profilesPopupAdapter, profilesManager);
        ItemTouchHelper helper = new ItemTouchHelper(touchHelper);
        helper.attachToRecyclerView(recyclerView);

        //ADD PROFILE
        RelativeLayout addBtn = popupView.findViewById(R.id.addProfileBg);
        addBtn.setBackground(Utils.getRandomGradientDrawable());
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(addBtn);
                new EditProfilePopup(kifflarm, profilesManager, ProfilesPopup.this, new Profile(kifflarm, profilesManager), true);
            }
        });

        ImageView addIcon = popupView.findViewById(R.id.addProfileIcon);
        addIcon.setImageDrawable(new DrawablePlus());

        showAtLocation(popupWindow);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                kifflarm.updateProfilesUI();
            }
        });
    }

    /** GET **/
    public ProfilesPopupAdapter getProfilesPopupAdapter() {
        return profilesPopupAdapter;
    }
    /** SET **/

    /** ADAPTER **/
    public void insertLastInAdapter(){
        profilesPopupAdapter.notifyItemInserted(profilesPopupAdapter.getItemCount());
    }
    public void notifyAdapter(){
        profilesPopupAdapter.notifyDataSetChanged();
    }
}
