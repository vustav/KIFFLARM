package com.kiefer.kifflarm.alarm;

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

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {
    protected KIFFLARM kifflarm;
    protected Alarmist alarmist;

    public AlarmsAdapter(KIFFLARM kifflarm, Alarmist alarmist) {
        this.kifflarm = kifflarm;
        this.alarmist = alarmist;
    }

    public void onResume(){
        //explanation in MainView.onResume
        notifyDataSetChangedLocal();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_holder_alarm, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.e("AlarmsAdapter ZZZ", "onBind");

        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable());

        //Alarm alarm = kifflarm.getAlarm(viewHolder.getAdapterPosition());
        Alarm alarm = alarmist.getAlarm(viewHolder.getAdapterPosition());

        if(!alarm.isSnooze()){
            viewHolder.snoozeIndicatorTV.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.snoozeIndicatorTV.setVisibility(View.VISIBLE);
        }

        viewHolder.mainTV.setText(alarm.getTimeAsString());
        viewHolder.mainTV.setBackground(Utils.getRandomGradientDrawable());

        activateVH(viewHolder, alarm.isActive());
        viewHolder.toggleBtn.setVisibility(View.VISIBLE);
        viewHolder.toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(v);
                if(!alarm.isSnooze()) {
                    alarm.activate(!alarm.isActive());
                    //alarm.saveAndSchedule();
                    activateVH(viewHolder, alarm.isActive());
                } else{
                    removeAlarm(viewHolder.getAdapterPosition());
                }
            }
        });

        //no need to edit a snooze
        if(!alarm.isSnooze()) {
            viewHolder.mainBtn.setOnClickListener(v -> openAlarmDialog(this, viewHolder.getAdapterPosition()));
        }
        else{
            viewHolder.mainBtn.setOnClickListener(null);
        }

        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(v);
                removeAlarm(viewHolder.getAdapterPosition());
            }
        });
        //viewHolder.delTV.setBackground(Utils.getRandomGradientDrawable());
    }

    public void removeAlarm(int i){
        alarmist.removeAlarm(i);
        notifyItemRemoved(i);
    }

    public void activateVH(ViewHolder viewHolder, boolean on){
        //Log.e("AlarmsAdapter ZZZ", "activate: "+on);

        if(on) {
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOn, null));
            viewHolder.toggleIndicator.setAlpha(1);
            viewHolder.delBtn.setAlpha(1);
            //viewHolder.delTV.setAlpha(1);
            viewHolder.delIV.setImageAlpha(255);
            viewHolder.mainBtn.setAlpha(1);
            viewHolder.mainTV.setAlpha(1);
            viewHolder.toggleBtn.setAlpha(1);
        }
        else{
            float alpha = .5f;
            int alphaInt = (int)(255f * alpha);
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOff, null));
            viewHolder.toggleIndicator.setAlpha(alpha);
            viewHolder.delBtn.setAlpha(alpha);
            //viewHolder.delTV.setAlpha(alpha);
            viewHolder.delIV.setImageAlpha(alphaInt);
            viewHolder.mainBtn.setAlpha(alpha);
            viewHolder.mainTV.setAlpha(alpha);
            viewHolder.toggleBtn.setAlpha(alpha);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return alarmist.getItemCount();
    }

    /** POPUPS **/
    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, int index){
        new SetAlarmPopup(kifflarm, alarmist, alarmsAdapter, alarmist.getAlarm(index), false);
    }

    public void openNewAlarmDialog(AlarmsAdapter alarmsAdapter){
        new SetAlarmPopup(kifflarm, alarmist, alarmsAdapter, new Alarm(kifflarm, kifflarm.getSoundManager().getRandomSound(), ((AlarmManager)alarmist).getFolder()), true);
    }

    /** ADAPTER **/
    public void notifyDataSetChangedLocal(){
        super.notifyDataSetChanged();
    }

    //Param alarm is used by subclass AlarmsAdapterProfiles, fix this.
    public void notifyItemInsertedLocal(int index, Alarm alarm){
        super.notifyItemInserted(index);
    }

    /** VIEWHOLDEr **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mainLayout;
        public RelativeLayout bg;
        private final TextView mainTV, snoozeIndicatorTV;
        //private final TextView delTV;
        private final Button mainBtn, delBtn;
        //private final SwitchMaterial toggle;
        private final Button toggleBtn;
        public final RelativeLayout toggleBtnLayout, deleteBtnLayout;
        private final FrameLayout toggleIndicator;
        //private int toggleCheck = 0; //seems to be needed to not trigger the toggle in onBindViewHolder
        private ImageView delIV;

        public ViewHolder(View view) {
            super(view);
            mainLayout = view.findViewById(R.id.alarmsVHMainLayout);
            bg = view.findViewById(R.id.alarmVHBg);
            mainTV = view.findViewById(R.id.alarmVHMainBtnTextView);
            mainBtn = view.findViewById(R.id.alarmVHMainButton);
            delBtn = view.findViewById(R.id.alarmVHRemoveButton);
            deleteBtnLayout = view.findViewById(R.id.alarmsVHDelBtnLayout);
            //toggle = view.findViewById(R.id.alarmsVHToggle);
            toggleBtn = view.findViewById(R.id.alarmVHToggleButton);
            toggleBtnLayout = view.findViewById(R.id.alarmsVHToggleBtnLayout);
            //delTV = view.findViewById(R.id.alarmVHRemoveTextView);
            snoozeIndicatorTV = view.findViewById(R.id.alarmVHSnoozeIndicatorTV);
            toggleIndicator = view.findViewById(R.id.alarmsVHToggleButtonIndicator);
            //checkBox = view.findViewById(R.id.alarmsVHCheck);
            delIV = view.findViewById(R.id.alarmVHRemoveIV);
        }
    }
}