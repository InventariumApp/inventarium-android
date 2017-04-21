package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventariumapp.inventarium.R;
import com.inventariumapp.inventarium.Utility.Item;

public class ManualInput extends AppCompatActivity {
    private String list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        list = intent.getStringExtra("list");
        String message = intent.getStringExtra("message");

        if (message != null) {
            EditText text = (EditText) findViewById(R.id.name);
            text.setText(message);
        }
    }

    public void clickCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void clickAdd(View view) {
        // Firebase dataBase
        DatabaseReference mFirebaseDatabaseReference;
        EditText count = (EditText)findViewById(R.id.count);
        EditText name = (EditText)findViewById(R.id.name);
        if (name.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a item name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (count.getText().toString().matches("")) {
            Toast.makeText(this, "You did not enter a count", Toast.LENGTH_SHORT).show();
            return;
        }
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
        //Item item = new Item(name.getText().toString(), Integer.parseInt(count.getText().toString()),user);
        if (list.equals("0")) { // Pantry
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("pantry-list");
           // mFirebaseDatabaseReference.child(name.getText().toString()).setValue(item);
        }
        else if (list.equals("1")) { // ShoppingList
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("shopping-list");
          //  mFirebaseDatabaseReference.child(name.getText().toString()).setValue(item);
        }
        else {
            Log.i("ManualInput.clickAdd", "unknown tab position");
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
