package com.example.kifflarm.popups;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.R;
import com.example.kifflarm.drawables.DrawablePlay;
import com.example.kifflarm.drawables.DrawableStop;
import com.example.kifflarm.sound.Sound;
import com.example.kifflarm.Utils;
import com.example.kifflarm.alarm.Alarm;
import com.example.kifflarm.sound.SoundManager;

public class SoundPopupAdapter extends RecyclerView.Adapter<SoundPopupAdapter.ViewHolder> {
    private SoundPopup soundPopup;
    private SoundManager soundManager;
    private Alarm alarm;
    private AlarmSettingsPopup alarmSettingsPopup;

    private RecyclerView recyclerView;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg, tvBg;
        private final TextView tv;

        private final Button playBtn;

        private final ImageView playBtnIcon;

        private final DrawablePlay drawablePlay = new DrawablePlay();
        private final DrawableStop drawableStop = new DrawableStop();

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.soundVHBG);
            tvBg = view.findViewById(R.id.soundVHTVLayout);
            tv = view.findViewById(R.id.soundVHTV);
            playBtn = view.findViewById(R.id.soundVHPlayBtn);
            playBtnIcon = view.findViewById(R.id.soundVHPlayBtnIcon);
        }

        private void setPlaying(boolean playing){
            if(playing){
                playBtnIcon.setImageDrawable(drawableStop);
            }
            else{
                playBtnIcon.setImageDrawable(drawablePlay);
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter
     */
    public SoundPopupAdapter(RecyclerView recyclerView, SoundManager soundManager, AlarmSettingsPopup alarmSettingsPopup, SoundPopup soundPopup, Alarm alarm) {
        this.recyclerView = recyclerView;
        this.soundPopup = soundPopup;
        this.alarmSettingsPopup = alarmSettingsPopup;
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
        //viewHolder.textView.setTextColor(Utils.getContrastColor(color));

        Sound sound = soundManager.getSounds().get(viewHolder.getAdapterPosition());
        viewHolder.tv.setText(sound.getName());

        viewHolder.tvBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm.setSound(sound);
                alarmSettingsPopup.setSoundBtnTxt(sound.getName());
                soundPopup.dismiss();
            }
        });

        viewHolder.setPlaying(false);
        viewHolder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soundPopup.getCurrentlyPreviewedSound().getName().equals(sound.getName())){
                    viewHolder.setPlaying(false);
                    soundPopup.stopPreview();
                }
                else{
                    for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
                        ViewHolder holder = (ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                        holder.setPlaying(false);
                    }
                    viewHolder.setPlaying(true);
                    soundPopup.playPreview(sound);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return soundManager.getSounds().size();
    }
}
