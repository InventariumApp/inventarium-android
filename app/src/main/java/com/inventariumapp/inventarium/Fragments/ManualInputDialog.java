package com.inventariumapp.inventarium.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inventariumapp.inventarium.R;
import com.inventariumapp.inventarium.Utility.Item;

import static com.inventariumapp.inventarium.R.id.container;

public class ManualInputDialog extends DialogFragment {

    private EditText count;
    private Button minusButton;
    private Button plusButton;
    private TextView cancel;
    private TextView add;
    private AutoCompleteTextView text;
    private int list;

    public static ManualInputDialog newInstance(int num) {
        ManualInputDialog f = new ManualInputDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("list", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.fragment_manual_input_dialog, null);
        count = (EditText) rootView.findViewById(R.id.count_num);
        minusButton = (Button) rootView.findViewById(R.id.btn_minus);
        plusButton = (Button) rootView.findViewById(R.id.btn_plus);
        cancel = (TextView) rootView.findViewById(R.id.cancel_dialog);
        add = (TextView) rootView.findViewById(R.id.add_dialog);
        text = (AutoCompleteTextView) rootView.findViewById(R.id.categoryText);

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
                // Firebase dataBase
                DatabaseReference mFirebaseDatabaseReference;

                if (text.getText().toString().matches("")) {
                    Toast.makeText(getContext(), "You did not enter a item name", Toast.LENGTH_SHORT).show();
                    return;
                }
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace('.', ',');
                Item item = new Item(text.getText().toString(), Integer.parseInt(count.getText().toString()),user);
                if (list == 0) { // Pantry
                    mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("lists").child(user).child("pantry-list");
                    mFirebaseDatabaseReference.child(text.getText().toString()).setValue(item);
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
        });
        builder.setView(rootView);

        return builder.create();
    }
}
