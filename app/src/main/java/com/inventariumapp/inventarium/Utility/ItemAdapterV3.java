package com.inventariumapp.inventarium.Utility;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.x;
import static android.R.id.list;

/**
 * Created by Yousef on 3/24/2017.
 */

public class ItemAdapterV3 extends FirebaseRecyclerAdapter<Item, ItemHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1000; // 1sec

    // Keep track of items and their removals
    private List<String> itemsPendingRemoval;

    // Track Swipe Dir
    public String swipeDir;

    // Use a thread to handle removles so that the user can undo their action
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>();
    private Handler handler = new Handler();

    private Context context;
    private String listName;

    public ItemAdapterV3(Class<Item> modelClass, int modelLayout, Class<ItemHolder> viewHolderClass, DatabaseReference ref, Context context, String listName) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.context = context;
        this.listName = listName;
        itemsPendingRemoval = new ArrayList<>();
    }


    @Override
    public void populateViewHolder(ItemHolder itemViewHolder, final Item item, final int position) {
        Log.i("popVew", item.getName());
        Log.i("popViewContains", String.valueOf(itemsPendingRemoval.contains(item.getName())));
        // Handles removal
        // Shows undo and paints background red
        final DatabaseReference ref = getRef(position);
        if (itemsPendingRemoval.contains(item.getName())) {

            if (swipeDir.equals("RIGHT")) {
                itemViewHolder.itemView.setBackgroundColor(Color.GREEN);
                itemViewHolder.getView().setVisibility(View.GONE);
                itemViewHolder.getUndoMoveButton().setVisibility(View.VISIBLE);
                itemViewHolder.getUndoDeleteButton().setVisibility(View.GONE);
            }
            if (swipeDir.equals("LEFT")) {
                itemViewHolder.itemView.setBackgroundColor(Color.RED);
                itemViewHolder.getView().setVisibility(View.GONE);
                itemViewHolder.getUndoDeleteButton().setVisibility(View.VISIBLE);
                itemViewHolder.getUndoMoveButton().setVisibility(View.GONE);
            }


            // Handle the undo
            itemViewHolder.getUndoDeleteButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item.getName());
                    pendingRunnables.remove(item.getName());
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(item.getName());
                    notifyItemChanged(position);
                }
            });

            // Handle the undo
            itemViewHolder.getUndoMoveButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item.getName());
                    pendingRunnables.remove(item.getName());
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(item.getName());
                    notifyItemChanged(position);
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

    // Used to remove an Item
    // Used on swipe
    public void addToPendingRemoval(final int position, final boolean move) {
        final Item item = this.getItem(position);
        Log.i("addToPendingRemoval", item.getName());
        if (!itemsPendingRemoval.contains(item.getName())) {
            itemsPendingRemoval.add(item.getName());
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    removeItem(item.getName(), move);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item.getName(), pendingRemovalRunnable);
        }
    }

    // Used to remove an Item
    // Used by addToPendingRemoval
    private void removeItem(String name, boolean move) {
        int position = 0;
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getName().equals(name)) {
                position = i;
                break;
            }
        }

        Item item = getItem(position);

        if (itemsPendingRemoval.contains(name)) {
            itemsPendingRemoval.remove(name);
        }
        if (move) {
            if(listName.equals("pantry")) {
                getRef(position).getParent().getParent().child("shopping-list").child(item.getName()).setValue(item);
            }
            else if(listName.equals("shoppingList")) {
                getRef(position).getParent().getParent().child("pantry-list").child(item.getName()).setValue(item);
            }
            else {
                Log.i("ERROR", "Invalid List!");
            }
        }

        getRef(position).removeValue();


    }

    public boolean isPendingRemoval(int position) {
        Item item = this.getItem(position);
        return itemsPendingRemoval.contains(item.getName());
    }


}
