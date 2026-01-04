package com.example.kifflarm.alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kifflarm.R;
import com.example.kifflarm.Utils;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder> {
    private AlarmManager alarmManager;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout bg;
        private final TextView textView;
        private final Button button, delBtn;
        private final CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            bg = view.findViewById(R.id.alarmVHBg);
            textView = view.findViewById(R.id.alarmVHtextView);
            button = view.findViewById(R.id.alarmVHbutton);
            delBtn = view.findViewById(R.id.alarmVHRemovebutton);
            checkBox = view.findViewById(R.id.alarmsVHCheck);
        }

        public TextView getTextView() {
            return textView;
        }

        public Button getButton(){
            return button;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     */
    public AlarmsAdapter(AlarmManager alarmManager) {
        this.alarmManager = alarmManager;
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
        viewHolder.bg.setBackground(Utils.getRandomGradientDrawable());

        Alarm alarm = alarmManager.getAlarms().get(position);
        viewHolder.getTextView().setText(alarm.getTimeAsString());

        activateVH(viewHolder, alarmManager.getAlarmActive(position));
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarmManager.setAlarmActive(viewHolder.getAdapterPosition(), isChecked);
            activateVH(viewHolder, isChecked);
        });

        viewHolder.button.setOnClickListener(v -> alarmManager.openAlarmDialog(this, viewHolder.getAdapterPosition(), false));
        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.removeAlarm(viewHolder.getAdapterPosition());
                notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
    }

    public void activateVH(ViewHolder viewHolder, boolean on){
        Log.e("AlarmsAdapter ZZZ", "on: "+on);
        viewHolder.checkBox.setChecked(on);

        if(on) {
            viewHolder.delBtn.setAlpha(1);
            viewHolder.button.setAlpha(1);
            viewHolder.textView.setAlpha(1);
        }
        else{
            float alpha = .5f;
            viewHolder.delBtn.setAlpha(alpha);
            viewHolder.button.setAlpha(alpha);
            viewHolder.textView.setAlpha(alpha);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return alarmManager.getAlarms().size();
    }
}