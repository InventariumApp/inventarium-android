package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventariumapp.inventarium.R;
import com.inventariumapp.inventarium.Utility.Item;

public class ManualInput extends AppCompatActivity {
    // Firebase dataBase
    private DatabaseReference mFirebaseDatabaseReference;

    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra("message");
        Log.i("message", message);
    }

    public void clickCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clickAdd(View view) {

        EditText count = (EditText)findViewById(R.id.count);
        EditText name = (EditText)findViewById(R.id.name);
        Item item = new Item(name.getText().toString(), Integer.parseInt(count.getText().toString()),"iphoneaccount@gmail,com");
        if (message.equals("0")) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child("iphoneaccount@gmail,com").child("pantry-list");
            mFirebaseDatabaseReference.child(name.getText().toString()).setValue(item);
        }
        else if (message.equals("1")) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child("iphoneaccount@gmail,com").child("shopping-list");
            mFirebaseDatabaseReference.child(name.getText().toString()).setValue(item);
        }
        else {
            Log.i("ManualInput.clickAdd", "unknown tab position");
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
