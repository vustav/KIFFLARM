package com.kiefer.kifflarm.profiles;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;

public class ProfilesPopupAdapter extends RecyclerView.Adapter<ProfilesPopupAdapter.ViewHolder> {
    //private final ProfilesPopup profilesPopup;
    private final KIFFLARM kifflarm;

    private final ProfilesPopup profilesPopup;
    private final ProfilesManager profilesManager;
    private final RecyclerView recyclerView;

    public ProfilesPopupAdapter(KIFFLARM kifflarm, ProfilesPopup profilesPopup, RecyclerView recyclerView, ProfilesManager profilesManager) {
        this.kifflarm = kifflarm;
        this.recyclerView = recyclerView;
        this.profilesPopup = profilesPopup;
        //this.profilesPopup = profilesPopup;
        this.profilesManager = profilesManager;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_holder_profiles, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProfilesPopupAdapter.ViewHolder viewHolder, final int position) {

        Profile profile = profilesManager.getProfiles().get(viewHolder.getAdapterPosition());

        if(viewHolder.gradientDrawable == null) {
            int color = Utils.getRandomColor();
            viewHolder.gradientDrawable = Utils.getRandomGradientDrawable(color, Utils.getRandomColor());
            viewHolder.bg.setBackground(viewHolder.gradientDrawable);
            viewHolder.tv.setTextColor(Utils.getContrastColor(color));
        }
        viewHolder.iv.setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), profile.getIconId(), null));
        viewHolder.tv.setText(profilesManager.getProfiles().get(viewHolder.getAdapterPosition()).getName());

        viewHolder.tvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activateProfile(viewHolder, profile);
                new EditProfilePopup(kifflarm, profilesManager, profilesPopup, profilesManager.getProfiles().get(viewHolder.getAdapterPosition()), false);
            }
        });

        //toggle
        viewHolder.toggleBtn.setVisibility(View.VISIBLE);
        viewHolder.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    activateProfile(viewHolder, profile);
            }
        });
        activateVH(viewHolder, profile.isActive());

        //delete
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile(viewHolder.getAdapterPosition());
            }
        });

        //edit
        /*
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfilePopup(kifflarm, profilesManager, profilesPopup, profilesManager.getProfiles().get(viewHolder.getAdapterPosition()), false);
            }
        });

         */

        //quick
        viewHolder.quickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewHolder.getAdapterPosition();
                Utils.performHapticFeedback(viewHolder.itemView);
                profilesManager.setQuick(index, !profilesManager.isQuick(index));
                updateDesktopIndicator(profilesManager.isQuick(index), viewHolder.quickIndicator);

                //kifflarm.updateProfilesUI();
            }
        });
        updateDesktopIndicator(profilesManager.isQuick(viewHolder.getAdapterPosition()), viewHolder.quickIndicator);
    }

    public void deleteProfile(int index){
        profilesManager.delete(index);
        notifyItemRemoved(index);
    }

    private void updateDesktopIndicator(boolean on, FrameLayout indicator){
        if(on) {
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOn, null));
        }
        else{
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOff, null));
        }
    }

    private void activateProfile(ProfilesPopupAdapter.ViewHolder viewHolder, Profile profile){
        profilesManager.activateProfile(profile, !profile.isActive());
        activateVH(viewHolder, profile.isActive());
        notifyDataSetChanged();
    }

    public void activateVH(ProfilesPopupAdapter.ViewHolder viewHolder, boolean on){

        if(on) {
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOn, null));
            //viewHolder.toggleIndicator.setAlpha(1);
            //viewHolder.delBtn.setAlpha(1);
            //viewHolder.delTV.setAlpha(1);
            //viewHolder.delIV.setImageAlpha(255);
            //viewHolder.mainBtn.setAlpha(1);
            //viewHolder.mainTV.setAlpha(1);
            //viewHolder.toggleBtn.setAlpha(1);
        }
        else{
            float alpha = .5f;
            int alphaInt = (int)(255f * alpha);
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOff, null));
            //viewHolder.toggleIndicator.setAlpha(alpha);
            //viewHolder.delBtn.setAlpha(alpha);
            //viewHolder.delTV.setAlpha(alpha);
            //viewHolder.delIV.setImageAlpha(alphaInt);
            //viewHolder.mainBtn.setAlpha(alpha);
            //viewHolder.mainTV.setAlpha(alpha);
            //viewHolder.toggleBtn.setAlpha(alpha);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return profilesManager.getProfiles().size();
    }

    /** VIEWHOLDER **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private GradientDrawable gradientDrawable;
        private RelativeLayout bg, tvLayout;
        private final TextView tv;
        private final ImageView iv;
        private final Button deleteBtn, quickBtn, toggleBtn;
        //private final Button editBtn;
        private final FrameLayout quickIndicator, toggleIndicator;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.profilesVHBG);
            tvLayout = view.findViewById(R.id.profilesVHTVLayout);
            tv = view.findViewById(R.id.profilesVHTV);
            iv = view.findViewById(R.id.profilesVHIV);
            deleteBtn = view.findViewById(R.id.profilesVHDeleteBtn);
            quickBtn = view.findViewById(R.id.profilesVHAddQuickBtn);
            //editBtn = view.findViewById(R.id.profilesVHEditBtn);
            quickIndicator = view.findViewById(R.id.profilesVHAddQuickIndicator);
            toggleBtn = view.findViewById(R.id.profilesVHToggleButton);
            toggleIndicator = view.findViewById(R.id.profilesVHToggleButtonIndicator);
        }

        /*
        private void setPlaying(boolean playing) {
            if (playing) {
                playBtnIcon.setImageDrawable(drawableStop);
            } else {
                playBtnIcon.setImageDrawable(drawablePlay);
            }
        }

         */
    }
}
