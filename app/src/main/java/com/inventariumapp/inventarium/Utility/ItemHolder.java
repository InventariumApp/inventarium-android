package com.inventariumapp.inventarium.Utility;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inventariumapp.inventarium.Activities.ItemDetail;
import com.inventariumapp.inventarium.Activities.MainActivity;
import com.inventariumapp.inventarium.R;

/**
 * Created by Yousef on 3/23/2017.
 */

public class ItemHolder extends RecyclerView.ViewHolder {

    private TextView nameView;
    private TextView countView;
    private Button leftButton;
    private Button rightButton;

    public ItemHolder(View itemView) {
        super(itemView);
        nameView = (TextView) itemView.findViewById(R.id.item_text_view);
        countView = (TextView) itemView.findViewById(R.id.count_text_view);
        rightButton = (Button) itemView.findViewById(R.id.right_button);
        leftButton = (Button) itemView.findViewById(R.id.left_button);

        //itemView.setOnClickListener(listener);
    }

    public TextView getCountView() {
        return countView;
    }

    public TextView getNameView() {return nameView; }

    public Button getRightButton() {
        return leftButton;
    }

    public Button getLeftButton() {
        return rightButton;
    }


}
