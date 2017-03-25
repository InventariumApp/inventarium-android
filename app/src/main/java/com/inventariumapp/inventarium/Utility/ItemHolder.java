package com.inventariumapp.inventarium.Utility;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inventariumapp.inventarium.R;

/**
 * Created by Yousef on 3/23/2017.
 */

public class ItemHolder extends RecyclerView.ViewHolder {

    private TextView view;
    private Button leftButton;
    private Button rightButton;

    public ItemHolder(View itemView) {
        super(itemView);
        view = (TextView) itemView.findViewById(R.id.item_text_view);
        rightButton = (Button) itemView.findViewById(R.id.right_button);
        leftButton = (Button) itemView.findViewById(R.id.left_button);
    }

    public TextView getView() {
        return view;
    }

    public Button getRightButton() {
        return leftButton;
    }

    public Button getLeftButton() {
        return rightButton;
    }
}
