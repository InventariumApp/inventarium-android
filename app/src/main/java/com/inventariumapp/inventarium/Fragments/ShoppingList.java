package com.inventariumapp.inventarium.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventariumapp.inventarium.R;
import com.inventariumapp.inventarium.Utility.ItemAdapter;
import com.inventariumapp.inventarium.Utility.ItemHandler;
import com.inventariumapp.inventarium.Utility.ItemHolder;
import com.inventariumapp.inventarium.Utility.Item;

/**
 * Created by Yousef on 2/18/2017.
 */

public class ShoppingList extends Fragment {

    // Firebase dataBase
    private DatabaseReference mFirebaseDatabaseReference;

    RecyclerView mRecyclerView;

    @Override
    // Returns a View from this method that is the root of your fragment's layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab2_shopping_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.shopping_list_recycler_view);

        // Set Up recycler view
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(false);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child("iphoneaccount@gmail,com").child("shopping-list");
        ItemAdapter adapter = new ItemAdapter(Item.class, R.layout.item_row_view, ItemHolder.class, mFirebaseDatabaseReference, "shoppingList");
        mRecyclerView.setAdapter(adapter);

        // Set up animations and touch events
        ItemHandler itemHandler = new ItemHandler(mRecyclerView, getActivity(), "shoppingList");
        itemHandler.setUpItemTouchHelper();
        itemHandler.setUpAnimationDecoratorHelper();
        return rootView;
    }
}
