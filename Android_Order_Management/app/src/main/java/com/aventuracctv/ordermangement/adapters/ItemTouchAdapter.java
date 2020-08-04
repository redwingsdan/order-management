package com.aventuracctv.ordermangement.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by jburmeister on 4/27/2016.
 */
public class ItemTouchAdapter extends ItemTouchHelper.SimpleCallback {
    private BuildListAdapter mBuildListAdapter;

    public ItemTouchAdapter(BuildListAdapter buildListAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mBuildListAdapter = buildListAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mBuildListAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
    }
}