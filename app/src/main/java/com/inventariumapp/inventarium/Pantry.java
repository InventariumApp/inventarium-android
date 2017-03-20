package com.inventariumapp.inventarium;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Yousef on 2/18/2017.
 */

public class Pantry extends Fragment {
    RecyclerView myView;

    @Override
    // Returns a View from this method that is the root of your fragment's layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View rootView = inflater.inflate(R.layout.tab1_pantry, container, false);
        myView = (RecyclerView) rootView.findViewById(R.id.pantry_recycler_view);


        return rootView;
    }
}