package com.inventariumapp.inventarium.Utility;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yousef on 3/24/2017.
 */

public class ItemAdapter extends FirebaseRecyclerAdapter<Item, ItemHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 1000; // 1sec

    // Keep track of items and their removals
    private List<String> itemsPendingRemoval;

    // Track Swipe Dir
    public enum SwipDir {RIGHT, LEFT};
    private SwipDir swipeDir;

    // Use a thread to handle removals so that the user can undo their action
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>();
    private Handler handler = new Handler();

    // Reference to the fragment
    // Either "shoppingList" or "pantry
    private String listName;

    public ItemAdapter(Class<Item> modelClass, int modelLayout, Class<ItemHolder> viewHolderClass, DatabaseReference ref, String listName) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.listName = listName;
        itemsPendingRemoval = new ArrayList<>();
    }

    @Override
    public void populateViewHolder(ItemHolder itemViewHolder, final Item item, final int position) {
        // Handles removal
        // Shows undo and paints background red or green accordingly
        if (itemsPendingRemoval.contains(item.getName())) {
            if (listName.equals("pantry")) {
                if (swipeDir == SwipDir.LEFT) {
                    swipeToDelete(itemViewHolder);
                }
                else {
                    swipeToMove(itemViewHolder);
                }
            }
            else { // shoppingList
                if (swipeDir == SwipDir.LEFT) {
                    swipeToMove(itemViewHolder);
                }
                else {
                    swipeToDelete(itemViewHolder);
                }
            }

            // Handle the right button
            itemViewHolder.getRightButton().setOnClickListener(new View.OnClickListener() {
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

            // Handle the left button
            itemViewHolder.getLeftButton().setOnClickListener(new View.OnClickListener() {
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
            itemViewHolder.getNameView().setVisibility(View.VISIBLE);
            itemViewHolder.getNameView().setText(item.getName());
            itemViewHolder.getCountView().setVisibility(View.VISIBLE);
            itemViewHolder.getCountView().setText(Integer.toString(item.getCount()));
            itemViewHolder.getRightButton().setVisibility(View.GONE);
            itemViewHolder.getRightButton().setOnClickListener(null);
            itemViewHolder.getLeftButton().setVisibility(View.GONE);
            itemViewHolder.getLeftButton().setOnClickListener(null);
        }
    }

    // Colors and reveals swipe to move layout
    private void swipeToMove(ItemHolder itemViewHolder) {
        itemViewHolder.itemView.setBackgroundColor(Color.GREEN);
        itemViewHolder.getNameView().setVisibility(View.GONE);
        itemViewHolder.getCountView().setVisibility(View.GONE);

        if (listName.equals("shoppingList")) {
            itemViewHolder.getLeftButton().setVisibility(View.VISIBLE);
            itemViewHolder.getRightButton().setVisibility(View.GONE);
        }
        else {
            itemViewHolder.getLeftButton().setVisibility(View.GONE);
            itemViewHolder.getRightButton().setVisibility(View.VISIBLE);
        }
    }

    // Colors and reveals swipe to delete layout
    private void swipeToDelete(ItemHolder itemViewHolder) {
        itemViewHolder.itemView.setBackgroundColor(Color.RED);
        itemViewHolder.getNameView().setVisibility(View.GONE);
        itemViewHolder.getCountView().setVisibility(View.GONE);
        if (listName.equals("shoppingList")) {
            itemViewHolder.getLeftButton().setVisibility(View.GONE);
            itemViewHolder.getRightButton().setVisibility(View.VISIBLE);
        }
        else {
            itemViewHolder.getLeftButton().setVisibility(View.VISIBLE);
            itemViewHolder.getRightButton().setVisibility(View.GONE);
        }
    }

    // Sets swipe dir
    public void setSwipeDir(SwipDir dir) {
        swipeDir = dir;
    }

    // Returns swipe dir
    public SwipDir getSwipeDir() { return swipeDir;}


    // Used to remove an Item
    // Used on swipe
    public void addToPendingRemoval(final int position, final boolean move) {
        final Item item = this.getItem(position);
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
                Log.i("ItemAdapter.removeItem", "Invalid List!");
            }
        }

        getRef(position).removeValue();


    }

    // Checkd to see if item is pending removal
    public boolean isPendingRemoval(int position) {
        Item item = this.getItem(position);
        return itemsPendingRemoval.contains(item.getName());
    }
}
