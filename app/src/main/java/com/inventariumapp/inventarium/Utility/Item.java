package com.inventariumapp.inventarium.Utility;

/**
 * Created by Yousef on 3/19/2017.
 */

/**
 * Class to hold item data
 * Allows for setting and retrieving data values
 */
public class Item {

    private String name;
    private int count;
    private String addedByUser;
    private String imageURL;
    private String price;
    private String category;

    // Needed for Firebase
    public Item() {

    }
    public Item(String name, int count, String addedByUser, String imageURL, String price, String category) {
        this.name = name;
        this.count = count;
        this.addedByUser = addedByUser;
        this.imageURL = imageURL;
        this.price = price;
        this.category = category;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public String getAddedByUser() { return addedByUser;}

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setAddedByUser(String addedByUser) {this.addedByUser = addedByUser;}

}
