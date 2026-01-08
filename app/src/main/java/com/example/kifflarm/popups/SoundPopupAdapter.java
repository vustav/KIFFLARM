package com.example.kifflarm.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.R;
import com.example.kifflarm.sound.Sound;
import com.example.kifflarm.Utils;
import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.sound.SoundManager;

public class SoundPopupAdapter extends RecyclerView.Adapter<SoundPopupAdapter.ViewHolder> {
    private SoundPopup soundPopup;
    private SoundManager soundManager;
    private Alarm alarm;
    private AlarmPopup alarmPopup;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.soundVHBG);
            textView = view.findViewById(R.id.soundVHTV);
        }
    }

    /**
     * Initialize the dataset of the Adapter
     */
    public SoundPopupAdapter(SoundManager soundManager, AlarmPopup alarmPopup, SoundPopup soundPopup, Alarm alarm) {
        this.soundPopup = soundPopup;
        this.alarmPopup = alarmPopup;
        this.soundManager = soundManager;
        this.alarm = alarm;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_holder_sound, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SoundPopupAdapter.ViewHolder viewHolder, final int position) {
        int color = Utils.getRandomColor();
        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable(color, Utils.getRandomColor()));
        viewHolder.textView.setTextColor(Utils.getContrastColor(color));

        Sound sound = soundManager.getSounds().get(viewHolder.getAdapterPosition());
        viewHolder.textView.setText(sound.getName());

        viewHolder.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setSound(sound);
                alarmPopup.setSoundBtnTxt(sound.getName());
                soundPopup.dismiss();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return soundManager.getSounds().size();
    }
}
