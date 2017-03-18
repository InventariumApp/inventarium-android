package com.inventariumapp.inventarium;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    ArrayList<String> shoppingList = null;
    ArrayAdapter adapter = null;
    ListView lv = null;

    // Keeps track of swipes
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        View rootView = inflater.inflate(R.layout.tab1_pantry, container, false);
        shoppingList = new ArrayList<>();
        Collections.addAll(shoppingList, "Eggs", "Yogurt", "Milk", "Bananas", "Apples", "Tide with bleach", "Cascade");
        shoppingList.addAll(Arrays.asList("Napkins", "Dog food", "Chapstick", "Bread"));
        shoppingList.add("Sunscreen");
        shoppingList.add("Toothpaste");
        adapter = new ArrayAdapter(container.getContext(), android.R.layout.simple_list_item_1, shoppingList);
        lv = (ListView) rootView.findViewById(R.id.pantry_content);
        lv.setAdapter(adapter);
        return rootView;
    }
}