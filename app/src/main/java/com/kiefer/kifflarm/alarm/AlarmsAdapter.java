package com.kiefer.kifflarm.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_holder_alarm, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        //Log.e("AlarmsAdapter ZZZ", "onBindViewHolder");
        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable());

        Alarm alarm = kifflarm.getAlarms().get(viewHolder.getAdapterPosition());

        viewHolder.mainTV.setText(alarm.getTimeAsString());
        viewHolder.mainTV.setBackground(Utils.getRandomGradientDrawable());

        activateVH(viewHolder, alarm.isActive());
        viewHolder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.activate(isChecked, true);
            activateVH(viewHolder, isChecked);
            Utils.performHapticFeedback(viewHolder.toggle);
        });

        viewHolder.mainBtn.setOnClickListener(v -> openAlarmDialog(this, viewHolder.getAdapterPosition(), false));
        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performHapticFeedback(v);
                removeAlarm(viewHolder.getAdapterPosition());
            }
        });
        viewHolder.removeTV.setBackground(Utils.getRandomGradientDrawable());
    }

    public void removeAlarm(int i){
        kifflarm.removeAlarm(i);
        notifyItemRemoved(i);
    }

    public void activateVH(ViewHolder viewHolder, boolean on){
        viewHolder.toggle.setChecked(on);

        if(on) {
            viewHolder.delBtn.setAlpha(1);
            viewHolder.mainBtn.setAlpha(1);
            viewHolder.mainTV.setAlpha(1);
        }
        else{
            float alpha = .5f;
            viewHolder.delBtn.setAlpha(alpha);
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

    /** VIEWHOLDEr **/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg;
        private final TextView mainTV, removeTV;
        private final Button mainBtn, delBtn;
        private final SwitchMaterial toggle;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.alarmVHBg);
            mainTV = view.findViewById(R.id.alarmVHtextView);
            mainBtn = view.findViewById(R.id.alarmVHbutton);
            delBtn = view.findViewById(R.id.alarmVHRemovebutton);
            toggle = view.findViewById(R.id.alarmsVHToggle);
            removeTV = view.findViewById(R.id.alarmVHRemovetextView);
        }
    }
}