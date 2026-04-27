package com.kiefer.kifflarm.profiles;

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

import java.util.ArrayList;

public class QuickProfilesAdapter extends RecyclerView.Adapter<QuickProfilesAdapter.ViewHolder> {
    private final KIFFLARM kifflarm;
    private final ProfilesManager profilesManager;
    //private ArrayList<Profile> quickProfiles;
    private final RecyclerView recyclerView;

    public QuickProfilesAdapter(KIFFLARM kifflarm, RecyclerView recyclerView, ProfilesManager profilesManager) {
        this.kifflarm = kifflarm;
        this.recyclerView = recyclerView;
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

        //int color = Utils.getRandomColor();
        //viewHolder.bg.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));
        //viewHolder.textView.setTextColor(Utils.getContrastColor(color));
        Profile profile = profilesManager.getQuickProfiles().get(viewHolder.getAdapterPosition());

        viewHolder.iv.setBackground(ResourcesCompat.getDrawable(kifflarm.getResources(), profile.getIconId(), null));

        viewHolder.tv.setText(profile.getShort());
    }

    @Override
    public int getItemCount() {
        return profilesManager.getQuickProfiles().size();
    }

    /** VIEWHOLDER **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv;

        public ViewHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.quickProfilesVHIV);
            tv = view.findViewById(R.id.quickProfilesVHTV);
        }
    }
}
