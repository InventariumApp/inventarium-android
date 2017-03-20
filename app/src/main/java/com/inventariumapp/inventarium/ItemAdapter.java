package com.inventariumapp.inventarium;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yousef on 3/19/2017.
 * Adapter with ViewHolder
 *
 */
public class ItemAdapter extends RecyclerView.Adapter {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    // Keep track of items and their removals
    private List<Item> items;
    private List<Item> itemsPendingRemoval;

    // Use a thread to handle removles so that the user can undo their action
    private HashMap<Item, Runnable> pendingRunnables = new HashMap<>();
    private Handler handler = new Handler();

    // Contstructer w/ dummy data
    // TODO: Change to Take in items from db
    public ItemAdapter() {
        items = new ArrayList<>();
        itemsPendingRemoval = new ArrayList<>();

        // Dummy Data
        Item a = new Item("Bannanna", 3);
        Item b = new Item("Apple", 1);
        Item c = new Item("Pineapple", 0);
        Item d = new Item("Chips", 10);
        items.add(a);
        items.add(b);
        items.add(c);
        items.add(d);
    }

    @Override
    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    // Describes an item view and metadata about its place within the RecyclerView.
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(parent);
    }

    @Override
    // Display the data at the specified position.
    // This method should update the contents of the itemView to reflect the item at the given position.
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final Item item = items.get(position);

        // Todo: Create option to move items between the two lists
        // Handles removal
        // Shows undo and paints background red
        if (itemsPendingRemoval.contains(item)) {
            viewHolder.itemView.setBackgroundColor(Color.RED);
            viewHolder.view.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);

            // Handle the undo
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null) {
                        handler.removeCallbacks(pendingRemovalRunnable);
                    }
                    itemsPendingRemoval.remove(item);
                    notifyItemChanged(items.indexOf(item));
                }
            });
        }
        // Not set to be removed, show it
        else{
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.view.setVisibility(View.VISIBLE);
            viewHolder.view.setText(item.getItemName());
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Used to add an Item
    // Method to be used for the three input methods
    public void addItem(Item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    // Used to remove an Item
    // Used on swipe
    public void addToPendingRemoval(int position) {
        final Item item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    removeItem(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    // Used to remove an Item
    // Used by addToPendingRemoval
    private void removeItem(int position) {
        Item item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        Item item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    /**
     * Custom ViewHolder
     * Contains textView and undo button
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView view;
        Button undoButton;

        public ItemViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_view, parent, false));
            view = (TextView) itemView.findViewById(R.id.item_text_view);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }
    }
}
