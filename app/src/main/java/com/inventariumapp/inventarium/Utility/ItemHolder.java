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
    private Button undoDeleteButton;
    private Button undoMoveButton;

    public ItemHolder(View itemView) {
        super(itemView);
        view = (TextView) itemView.findViewById(R.id.item_text_view);
        undoDeleteButton = (Button) itemView.findViewById(R.id.undo_delete_button);
        undoMoveButton = (Button) itemView.findViewById(R.id.undo_move_button);
    }

    public TextView getView() {
        return view;
    }

    public Button getUndoDeleteButton() {
        return undoDeleteButton;
    }

    public Button getUndoMoveButton() {
        return undoMoveButton;
    }
}
