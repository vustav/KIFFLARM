package com.kiefer.kifflarm.profiles;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

public class QuickProfilesTouchHelper extends ItemTouchHelper.SimpleCallback {
    private QuickProfilesAdapter adapter;
    private ProfilesManager profilesManager;
    private boolean dragging = false; //true while dragging to avoid updating until the drag is complete

    // true when onMove() is called. Otherwise a swipe will be made with the old dragStart-value when holding an item and releasing before onMove() is called (which happens when items trade places).
    public boolean dragDone = false;

    private int dragStart;

    public QuickProfilesTouchHelper(QuickProfilesAdapter adapter, ProfilesManager profilesManager){
        super(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        this.adapter = adapter;
        this.profilesManager = profilesManager;
    }

    //gets called every insert on a drag. After the first one dragStart gets setChecked, then dragging is
    //true until the drag is complete and clearView() gets called, where the dragStop gets setChecked.
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //Log.e("GGG", "TrackTouchHelper.onMove()");

        if(!dragging){
            dragStart = viewHolder.getAdapterPosition();
            dragging = true;
        }
        dragDone = true;

        adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /*
    Called by the ItemTouchHelper when the user interaction with an element is over and it also
    completed its animation. Without a call to its super the chain won't update properly visually.
     */

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        int dragEnd = viewHolder.getAdapterPosition();

        //clearView is called after onMove so any drags or swipes are complete
        dragging = false;

        if(dragStart != dragEnd && dragDone){
            if(dragStart >= 0 && dragEnd >= 0) {
                profilesManager.moveQuickProfile(dragStart, dragEnd);
                dragDone = false;
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //
    }

    // DISABLE SWIPES
    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return 0;
    }
}