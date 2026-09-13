package com.kiefer.kifflarm.profiles;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;

public class QuickProfilesAdapter extends RecyclerView.Adapter<QuickProfilesAdapter.ViewHolder> {
    private final KIFFLARM kifflarm;
    private final ProfilesManager profilesManager;

    public QuickProfilesAdapter(KIFFLARM kifflarm, RecyclerView recyclerView, ProfilesManager profilesManager) {
        this.kifflarm = kifflarm;
        this.profilesManager = profilesManager;

        //nu hämtas arrayen varhe onBind. Om det är för segt måste det lösas bättre
        //quickProfiles = profilesManager.getQuickProfiles();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.viewholder_quick_profiles, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(QuickProfilesAdapter.ViewHolder viewHolder, final int position) {

        Profile profile = profilesManager.getQuickProfiles().get(viewHolder.getAdapterPosition());

        viewHolder.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(viewHolder.bg);
                profilesManager.activateQuickProfile(viewHolder.getAdapterPosition());
                kifflarm.updateProfilesUI();
            }
        });

        viewHolder.iv.setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), profile.getIconId(), null));

        viewHolder.tv.setText(profile.getShortLabel());
    }

    @Override
    public int getItemCount() {
        return profilesManager.getQuickProfiles().size();
    }

    /** VIEWHOLDER **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout bg;
        ImageView iv;
        TextView tv;

        public ViewHolder(View view) {
            super(view);
            bg = view.findViewById(R.id.quickProfilesVHBG);
            iv = view.findViewById(R.id.quickProfilesVHIV);
            tv = view.findViewById(R.id.quickProfilesVHTV);
        }
    }
}
