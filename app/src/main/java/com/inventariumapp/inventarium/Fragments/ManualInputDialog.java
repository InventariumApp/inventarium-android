package com.inventariumapp.inventarium.Fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inventariumapp.inventarium.R;
import com.inventariumapp.inventarium.Utility.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.inventariumapp.inventarium.R.id.container;

public class ManualInputDialog extends DialogFragment {

    private EditText count;
    private Button minusButton;
    private Button plusButton;
    private TextView cancel;
    private TextView add;
    private LinearLayout box;
    private AutoCompleteTextView text;
    private LinearLayout bottom;
    private int list; // 0 = Pantry, 1 = shopping list
    private String name;
    private final String url =  "https://inventarium.me/product_data_for_name/";

    public static ManualInputDialog newInstance(int num, String name) {
        ManualInputDialog f = new ManualInputDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("list", num);
        Log.i("list number is: ", Integer.toString(num));
        args.putString("name", name);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Num on create: ", Integer.toString(getArguments().getInt("list")));
        list = getArguments().getInt("list");
        name = getArguments().getString("name");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        list = getArguments().getInt("list");
        name = getArguments().getString("name");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.fragment_manual_input_dialog, null);
        count = (EditText) rootView.findViewById(R.id.count_num);
        minusButton = (Button) rootView.findViewById(R.id.btn_minus);
        plusButton = (Button) rootView.findViewById(R.id.btn_plus);
        cancel = (TextView) rootView.findViewById(R.id.cancel_dialog);
        add = (TextView) rootView.findViewById(R.id.add_dialog);
        text = (AutoCompleteTextView) rootView.findViewById(R.id.categoryText);
        box = (LinearLayout) rootView.findViewById(R.id.manual_input_dialog);
        bottom = (LinearLayout) rootView.findViewById(R.id.manual_input_bottom);
        //Log.i("Found The Name2!", name);

        // Firebase dataBase
        DatabaseReference mFirebaseDatabaseReference;
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("item-history");

        final HashSet<String> itemNames = new HashSet<>();

        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array

                Log.i("Snapshot data: ", dataSnapshot.toString());
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Log.i("item name", item.toString());
                    Log.i("item key", item.getKey());
                    String itemName = item.getKey();
                    itemNames.add(itemName);
                }

                ArrayList<String> itemList = new ArrayList<String>(itemNames);
                if(getActivity() != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, itemList);

                    Log.i("adapter array: ", itemNames.toString());
                    text.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        box.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Keyboard
                Rect r = new Rect();
                View view = getActivity().getWindow().getDecorView();
                view.getWindowVisibleDisplayFrame(r);
                int height = bottom.getHeight() +  r.top;
                text.setDropDownHeight(height);
            }
        });

        if (name != null) {
            Log.i("Found The Name3!", name);
            text.setText(name);
        }

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count.getText().toString().equals("1")) {
                    // Do Nothing
                }
                else {
                    int newCount = Integer.parseInt(count.getText().toString()) - 1;
                    count.setText(Integer.toString(newCount));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count.getText().toString().equals("10")) {
                    // Do Nothing
                }
                else {
                    int newCount = Integer.parseInt(count.getText().toString()) + 1;
                    count.setText(Integer.toString(newCount));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (text.getText().toString().matches("")) {
                    Toast.makeText(getContext(), "You did not enter a item name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("List # is:", Integer.toString(list));

                addItemDetails();
            }
        });
        builder.setView(rootView);

        return builder.create();
    }

    String productName;
    String price;
    String img;
    String category;

    private void addItemDetails() {
        // 0:name, 1:price, 2:imageURL
        final String[] itemDetails = new String[3];
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String urlEncodedName = text.getText().toString().replace(" ", "%20");
        Log.i("URL: ", url + urlEncodedName);
        String requestedItemDetails = url + urlEncodedName;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestedItemDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("HTTP Response: ", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("status")){
                                if(obj.get("status").toString().contains("No result for barcode")) {
                                    // showNotFound();
                                }
                            }
                            else {
                                Log.i("getting image", "!");
                                productName = obj.get("clean_nm").toString();
                                price = obj.get("price").toString();
                                img = obj.get("image_url").toString();
                                category = obj.get("category").toString();
                                sendDetails();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HTTP Get Err: ", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void sendDetails() {
        // Firebase dataBase
        DatabaseReference mFirebaseDatabaseReference;

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
        Item item = new Item(productName, Integer.parseInt(count.getText().toString()), user, img, price, category);
        if (list == 0) { // Pantry
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("pantry-list");
            mFirebaseDatabaseReference.child(text.getText().toString()).setValue(item);
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("item-history");
            mFirebaseDatabaseReference.child(productName.toLowerCase()).push().setValue(System.currentTimeMillis());
            mFirebaseDatabaseReference.child(productName.toLowerCase()).child("category").setValue(category);
        }
        else if (list == 1) { // ShoppingList
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("shopping-list");
            mFirebaseDatabaseReference.child(text.getText().toString()).setValue(item);
        }
        else {
            Log.i("ManualInput.clickAdd", "unknown tab position");
        }
        dismiss();
    }


}
