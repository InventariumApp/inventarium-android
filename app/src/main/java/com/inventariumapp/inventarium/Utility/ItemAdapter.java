package com.inventariumapp.inventarium.Utility;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventariumapp.inventarium.Activities.ItemDetail;
import com.inventariumapp.inventarium.Activities.MainActivity;
import com.inventariumapp.inventarium.R;

import org.json.JSONException;
import org.json.JSONObject;

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
    private SwipeDir swipeDir;

    // Use a thread to handle removals so that the user can undo their action
    private HashMap<String, Runnable> pendingRunnables = new HashMap<>();
    private Handler handler = new Handler();

    // Reference to the fragment
    // Either "shoppingList" or "pantry
    private String listName;

    private final Activity activity;

    private final String url =  "https://inventarium.me/product_data_for_name/";

    @Override
    public void onBindViewHolder(final ItemHolder viewHolder, final int position) {
        final Item item = getItem(position);
        super.onBindViewHolder(viewHolder, position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ItemDetail.class);

                View sharedView = view;
                String transitionName = "item_detail_transition";
                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transitionName);
                intent.putExtra("img", item.getImageURL());
                intent.putExtra("name", item.getName());
                intent.putExtra("price", item.getPrice());
                activity.startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // edit
                Toast.makeText(view.getContext(), "Item long click name: " + item.getName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

//    private void getItemDetails(String name, final Intent intent, final ActivityOptions transitionActivityOptions) {
//        // 0:name, 1:price, 2:imageURL
//        final String[] itemDetails = new String[3];
//        RequestQueue queue = Volley.newRequestQueue(activity);
//
//        String urlEncodedName = name.replace(" ", "%20");
//        Log.i("URL: ", url + urlEncodedName);
//        String requestedItemDetails = url + urlEncodedName;
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestedItemDetails,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i("HTTP Response: ", response);
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            if (obj.has("status")){
//                                if(obj.get("status").toString().contains("No result for barcode")) {
//                                    // showNotFound();
//                                }
//                            }
//                            else {
//                                Log.i("getting image", "!");
//                                String productName = obj.get("clean_nm").toString();
//                                itemDetails[0] = productName;
//                                String price = obj.get("price").toString();
//                                itemDetails[1] = price;
//                                String img = obj.get("image_url").toString();
//                                itemDetails[2] = img;
//                                sendItemDetails(itemDetails, intent, transitionActivityOptions);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("HTTP Get Err: ", error.toString());
//            }
//        });
//        queue.add(stringRequest);
//    }

    private void sendItemDetails(String[] details, Intent intent, ActivityOptions transitionActivityOptions) {
        // 0:name, 1:price, 2:imageURL
        intent.putExtra("name", details[0]);
        intent.putExtra("price", details[1]);
        intent.putExtra("img", details[2]);
        activity.startActivity(intent, transitionActivityOptions.toBundle());
    }
//    private void transitionToActivity(Class target, ItemHolder viewHolder, Item item) {
//        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
//                new Pair<>(viewHolder.binding.sampleIcon, "item_detail_transition"),
//                new Pair<>(viewHolder.binding.sampleName, activity.getString(R.string.sample_blue_title)));
//        activity.startActivity(target, pairs, item);
//    }


    public ItemAdapter(Activity activity, Class<Item> modelClass, int modelLayout, Class<ItemHolder> viewHolderClass, DatabaseReference ref, String listName) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.listName = listName;
        itemsPendingRemoval = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public void populateViewHolder(ItemHolder itemViewHolder, final Item item, final int position) {
        // Handles removal
        // Shows undo and paints background red or green accordingly
        if (itemsPendingRemoval.contains(item.getName())) {
            if (listName.equals("pantry")) {
                if (swipeDir == SwipeDir.LEFT) {
                    swipeToDelete(itemViewHolder);
                }
                else {
                    swipeToMove(itemViewHolder);
                }
            }
            else { // shoppingList
                if (swipeDir == SwipeDir.LEFT) {
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
    public void setSwipeDir(SwipeDir dir) {
        swipeDir = dir;
    }

    // Returns swipe dir
    public SwipeDir getSwipeDir() { return swipeDir;}


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
                getRef(position).getParent().getParent().child("item-history").child(getItem(position).getName().toLowerCase()).push().setValue(System.currentTimeMillis());
                getRef(position).getParent().getParent().child("item-history").child(getItem(position).getName().toLowerCase()).child("category").setValue(getItem(position).getCategory());
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
