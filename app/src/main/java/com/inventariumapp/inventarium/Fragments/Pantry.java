package com.inventariumapp.inventarium.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventariumapp.inventarium.Utility.ItemHandler;
import com.inventariumapp.inventarium.Utility.Item;
import com.inventariumapp.inventarium.Utility.ItemAdapter;
import com.inventariumapp.inventarium.R;

/**
 * Created by Yousef on 2/18/2017.
 */

public class Pantry extends Fragment {

    // Firebase dataBase
    private DatabaseReference mFirebaseDatabaseReference;

    RecyclerView mRecyclerView;

    @Override
    // Returns a View from this method that is the root of your fragment's layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View rootView = inflater.inflate(R.layout.tab1_pantry, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.pantry_recycler_view);

        // Set Up recycler view
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setAdapter(new ItemAdapter());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        // Set up animations and touch events
        ItemHandler itemHandler = new ItemHandler(mRecyclerView, getActivity());
        itemHandler.setUpItemTouchHelper();
        itemHandler.setUpAnimationDecoratorHelper();
        setUpDataBase();
        return rootView;
    }

    private void setUpDataBase() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Pantry");
        Log.i("DB ref", mFirebaseDatabaseReference.toString());
        Log.i("DB key", mFirebaseDatabaseReference.getKey().toString());
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object obj = snapshot.getValue();
                    Log.i("snapshot children value", obj.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}