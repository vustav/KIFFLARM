package com.kiefer.kifflarm.alarm;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.kiefer.kifflarm.utils.Utils;

public class AlarmsTouchHelper extends ItemTouchHelper.SimpleCallback {
    private AlarmsAdapter adapter;

    //Constructor
    public AlarmsTouchHelper(AlarmsAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    /*
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

     */

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        Utils.performHapticFeedback(viewHolder.itemView);
        adapter.removeAlarm(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    // Enable dragging
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    // Disable swiping
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /*
    @Override
    public int getMovementFlags(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

     */
}