package com.kiefer.kifflarm.profiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;

public class ProfilesPopupAdapter extends RecyclerView.Adapter<ProfilesPopupAdapter.ViewHolder> {
    //private final ProfilesPopup profilesPopup;
    private final KIFFLARM kifflarm;

    private final ProfilesListPopup profilesListPopup;
    private final ProfilesManager profilesManager;
    private final RecyclerView recyclerView;

    public ProfilesPopupAdapter(KIFFLARM kifflarm, ProfilesListPopup profilesListPopup, RecyclerView recyclerView, ProfilesManager profilesManager) {
        this.kifflarm = kifflarm;
        this.recyclerView = recyclerView;
        this.profilesListPopup = profilesListPopup;
        //this.profilesPopup = profilesPopup;
        this.profilesManager = profilesManager;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_holder_profiles_popup, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProfilesPopupAdapter.ViewHolder viewHolder, final int position) {

        int color = Utils.getRandomColor();
        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));
        //viewHolder.textView.setTextColor(Utils.getContrastColor(color));

        //Sound sound = soundManager.getSounds().get(viewHolder.getAdapterPosition());
        viewHolder.tv.setText(profilesManager.getProfiles().get(viewHolder.getAdapterPosition()).getName());
        viewHolder.tv.setTextColor(Utils.getContrastColor(color));

        viewHolder.tvLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditProfilePopup(kifflarm, profilesManager, profilesListPopup, profilesManager.getProfiles().get(viewHolder.getAdapterPosition()), false);
            }
        });

        //delete
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilesManager.delete(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });

        //quick
        viewHolder.quickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = viewHolder.getAdapterPosition();
                Utils.performHapticFeedback(viewHolder.itemView);
                profilesManager.setQuick(index, !profilesManager.getQuick(index));
                updateDesktopIndicator(profilesManager.getQuick(index), viewHolder.quickIndicator);

                kifflarm.getQuickProfilesAdapter().notifyDataSetChanged();
            }
        });
        updateDesktopIndicator(profilesManager.getQuick(viewHolder.getAdapterPosition()), viewHolder.quickIndicator);
    }

    private void updateDesktopIndicator(boolean on, FrameLayout indicator){
        if(on) {
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOn, null));
        }
        else{
            indicator.setBackgroundColor(kifflarm.getResources().getColor(R.color.indicatorOff, null));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return profilesManager.getProfiles().size();
    }

    /** VIEWHOLDER **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg, tvLayout;
        private final TextView tv;
        private final Button deleteBtn, quickBtn;
        private final FrameLayout quickIndicator;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.profilesVHBG);
            tvLayout = view.findViewById(R.id.profilesVHTVLayout);
            tv = view.findViewById(R.id.profilesVHTV);
            deleteBtn = view.findViewById(R.id.profilesVHDeleteBtn);
            quickBtn = view.findViewById(R.id.profilesVHAddBtn);
            quickIndicator = view.findViewById(R.id.profilesVHAddIndicator);
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
