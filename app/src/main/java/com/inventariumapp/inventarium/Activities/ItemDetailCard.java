package com.inventariumapp.inventarium.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.inventariumapp.inventarium.R;

public class ItemDetailCard extends Activity {

    TextView itemNameView;
    TextView purchaseLink;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_card);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        itemName = intent.getStringExtra("message");

        itemNameView = (TextView) findViewById(R.id.item_name);
        itemNameView.setText(itemName);
    }

    public void purchaseItem(View v) {

    }
}
