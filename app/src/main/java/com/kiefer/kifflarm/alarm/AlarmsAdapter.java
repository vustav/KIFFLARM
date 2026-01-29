package com.kiefer.kifflarm.alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.utils.Utils;
import com.kiefer.kifflarm.popups.SetAlarmPopup;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {
    private KIFFLARM kifflarm;

    public AlarmsAdapter(KIFFLARM kifflarm) {
        this.kifflarm = kifflarm;
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

        Alarm alarm = kifflarm.getAlarms().get(viewHolder.getAdapterPosition());

        if(!alarm.isSnooze()){
            viewHolder.snoozeIndicatorTV.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.snoozeIndicatorTV.setVisibility(View.VISIBLE);
        }

        viewHolder.mainTV.setText(alarm.getTimeAsString());
        viewHolder.mainTV.setBackground(Utils.getRandomGradientDrawable());

        activateVH(viewHolder, alarm.isActive());
        //viewHolder.toggleCheck = 0;

        //no need to edit a snooze
        if(!alarm.isSnooze()) {
            viewHolder.toggleBtn.setVisibility(View.VISIBLE);
            viewHolder.toggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarm.activate(!alarm.isActive());
                    alarm.saveAndSchedule();
                    activateVH(viewHolder, alarm.isActive());
                }
            });
        }
        else{
            //viewHolder.toggle.setVisibility(View.INVISIBLE);
            viewHolder.toggleBtn.setVisibility(View.INVISIBLE);
        }

        //no need to edit a snooze
        if(!alarm.isSnooze()) {
            viewHolder.mainBtn.setOnClickListener(v -> openAlarmDialog(this, viewHolder.getAdapterPosition(), false));
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
        viewHolder.delTV.setBackground(Utils.getRandomGradientDrawable());
    }

    public void removeAlarm(int i){
        kifflarm.removeAlarm(i);
        notifyItemRemoved(i);
    }

    public void activateVH(ViewHolder viewHolder, boolean on){
        //Log.e("AlarmsAdapter ZZZ", "activate: "+on);

        if(on) {
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOn, null));
            viewHolder.toggleIndicator.setAlpha(1);
            viewHolder.delBtn.setAlpha(1);
            viewHolder.delTV.setAlpha(1);
            viewHolder.mainBtn.setAlpha(1);
            viewHolder.mainTV.setAlpha(1);
            viewHolder.toggleBtn.setAlpha(1);
        }
        else{
            float alpha = .5f;
            viewHolder.toggleIndicator.setBackgroundColor(ResourcesCompat.getColor(kifflarm.getResources(), R.color.indicatorOff, null));
            viewHolder.toggleIndicator.setAlpha(alpha);
            viewHolder.delBtn.setAlpha(alpha);
            viewHolder.delTV.setAlpha(alpha);
            viewHolder.mainBtn.setAlpha(alpha);
            viewHolder.mainTV.setAlpha(alpha);
            viewHolder.toggleBtn.setAlpha(alpha);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return kifflarm.getAlarms().size();
    }

    /** POPUPS **/
    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, int index, boolean newAlarm){
        openAlarmDialog(alarmsAdapter, kifflarm.getAlarms().get(index), newAlarm);
    }

    public void openAlarmDialog(AlarmsAdapter alarmsAdapter, Alarm alarm, boolean newAlarm){
        new SetAlarmPopup(kifflarm, alarmsAdapter, alarm, newAlarm);
    }

    public void openNewAlarmDialog(AlarmsAdapter alarmsAdapter){
        openAlarmDialog(alarmsAdapter, new Alarm(kifflarm, kifflarm.getSoundManager().getRandomSound()), true);
    }

    public void notifyDataSetChangedLocal(){
        super.notifyDataSetChanged();
    }

    public void notifyItemInsertedLocal(int index){
        super.notifyItemInserted(index);
    }

    /** VIEWHOLDEr **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg;
        private final TextView mainTV, delTV, snoozeIndicatorTV;
        private final Button mainBtn, delBtn;
        //private final SwitchMaterial toggle;
        private final Button toggleBtn;
        private final FrameLayout toggleIndicator;
        //private int toggleCheck = 0; //seems to be needed to not trigger the toggle in onBindViewHolder

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.alarmVHBg);
            mainTV = view.findViewById(R.id.alarmVHMainBtnTextView);
            mainBtn = view.findViewById(R.id.alarmVHMainButton);
            delBtn = view.findViewById(R.id.alarmVHRemoveButton);
            //toggle = view.findViewById(R.id.alarmsVHToggle);
            toggleBtn = view.findViewById(R.id.alarmVHToggleButton);
            delTV = view.findViewById(R.id.alarmVHRemoveTextView);
            snoozeIndicatorTV = view.findViewById(R.id.alarmVHSnoozeIndicatorTV);
            toggleIndicator = view.findViewById(R.id.alarmsVHToggleButtonIndicator);
            //checkBox = view.findViewById(R.id.alarmsVHCheck);
        }
    }
}