package com.inventariumapp.inventarium;

/**
 * Created by Yousef on 3/19/2017.
 */

/**
 * Class to hold item data
 * Allows for setting and retrieving data values
 */
public class Item {

    private String itemName;
    private int itemCount;

    public Item(String name, int count) {
        itemName = name;
        itemCount = count;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemName(String name) {
        itemName = name;
    }

    public void setItemCount(int count) {
        itemCount = count;
    }


}
