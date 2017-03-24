package com.inventariumapp.inventarium.Utility;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.inventariumapp.inventarium.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Yousef on 3/24/2017.
 */

public class ItemAdapterv2 {

    public FirebaseRecyclerAdapter createAdapter(DatabaseReference mFirebaseDatabaseReference) {
        FirebaseRecyclerAdapter mAdapter = new FirebaseRecyclerAdapter<Item, ItemHolder>(Item.class, R.layout.item_row_view, ItemHolder.class, mFirebaseDatabaseReference) {
            private static final int PENDING_REMOVAL_TIMEOUT = 1000; // 1sec

            // Keep track of items and their removals
            private List<Item> itemsPendingRemoval;

            // Track Swipe Dir
            public String swipeDir;

            // Use a thread to handle removles so that the user can undo their action
            private HashMap<Item, Runnable> pendingRunnables = new HashMap<>();
            private Handler handler = new Handler();

            @Override
            public void populateViewHolder(ItemHolder itemViewHolder, final Item item, int position) {
                // Handles removal
                // Shows undo and paints background red
                if (itemsPendingRemoval.contains(item)) {
                    if (swipeDir == "RIGHT") {
                        itemViewHolder.getView().setBackgroundColor(Color.GREEN);
                        itemViewHolder.getView().setVisibility(View.GONE);
                        itemViewHolder.getUndoMoveButton().setVisibility(View.VISIBLE);
                        itemViewHolder.getUndoDeleteButton().setVisibility(View.GONE);
                    }
                    if (swipeDir == "LEFT") {
                        itemViewHolder.getView().setBackgroundColor(Color.RED);
                        itemViewHolder.getView().setVisibility(View.GONE);
                        itemViewHolder.getUndoDeleteButton().setVisibility(View.VISIBLE);
                        itemViewHolder.getUndoMoveButton().setVisibility(View.GONE);
                    }


                    // Handle the undo
                    itemViewHolder.getUndoDeleteButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                            pendingRunnables.remove(item);
                            if (pendingRemovalRunnable != null) {
                                handler.removeCallbacks(pendingRemovalRunnable);
                            }
                            itemsPendingRemoval.remove(item);
                            //notifyItemChanged(items.indexOf(item));
                        }
                    });

                    // Handle the undo
                    itemViewHolder.getUndoMoveButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                            pendingRunnables.remove(item);
                            if (pendingRemovalRunnable != null) {
                                handler.removeCallbacks(pendingRemovalRunnable);
                            }
                            itemsPendingRemoval.remove(item);
                            //notifyItemChanged(items.indexOf(item));
                        }
                    });
                }
                // Not set to be removed, show it
                else{
                    itemViewHolder.itemView.setBackgroundColor(Color.WHITE);
                    itemViewHolder.getView().setVisibility(View.VISIBLE);
                    itemViewHolder.getView().setText(item.getName() + " " + item.getCount());
                    itemViewHolder.getUndoDeleteButton().setVisibility(View.GONE);
                    itemViewHolder.getUndoDeleteButton().setOnClickListener(null);
                    itemViewHolder.getUndoMoveButton().setVisibility(View.GONE);
                    itemViewHolder.getUndoMoveButton().setOnClickListener(null);
                }
            }
        };

        return mAdapter;
    }
}
