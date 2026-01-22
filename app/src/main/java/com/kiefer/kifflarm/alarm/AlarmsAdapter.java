package com.kiefer.kifflarm.alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.KIFFLARM;
import com.kiefer.kifflarm.R;
import com.kiefer.kifflarm.Utils;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable());

        Alarm alarm = kifflarm.getAlarms().get(viewHolder.getAdapterPosition());

        viewHolder.mainTV.setText(alarm.getTimeAsString());
        viewHolder.mainTV.setBackground(Utils.getRandomGradientDrawable());

        activateVH(viewHolder, alarm.isActive());
        viewHolder.check = 0;

        viewHolder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //to avoid triggers on inBindViewHolder calls. Got random alarms activated, no idea what was going on.
            //Log.e("AlarmsAdapter ZZZ", "0");
            //int check = 0;
            if(++viewHolder.check > 0) {
                //Log.e("AlarmsAdapter ZZZ", "1");
                alarm.activate(isChecked, true, 1);
                activateVH(viewHolder, isChecked);
                Utils.performHapticFeedback(viewHolder.toggle);
            }
        });

        viewHolder.mainBtn.setOnClickListener(v -> openAlarmDialog(this, viewHolder.getAdapterPosition(), false));
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
        viewHolder.toggle.setChecked(on);

        if(on) {
            viewHolder.delBtn.setAlpha(1);
            viewHolder.delTV.setAlpha(1);
            viewHolder.mainBtn.setAlpha(1);
            viewHolder.mainTV.setAlpha(1);
        }
        else{
            float alpha = .5f;
            viewHolder.delBtn.setAlpha(alpha);
            viewHolder.delTV.setAlpha(alpha);
            viewHolder.mainBtn.setAlpha(alpha);
            viewHolder.mainTV.setAlpha(alpha);
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
        private RelativeLayout bg, shadow;
        private final TextView mainTV, delTV;
        private final Button mainBtn, delBtn;
        private final SwitchMaterial toggle;
        //private final CheckBox checkBox;

        private int check = 0;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.alarmVHBg);
            shadow = view.findViewById(R.id.alarmVHShadow);
            mainTV = view.findViewById(R.id.alarmVHtextView);
            mainBtn = view.findViewById(R.id.alarmVHbutton);
            delBtn = view.findViewById(R.id.alarmVHRemovebutton);
            toggle = view.findViewById(R.id.alarmsVHToggle);
            delTV = view.findViewById(R.id.alarmVHRemovetextView);
            //checkBox = view.findViewById(R.id.alarmsVHCheck);
        }
    }
}