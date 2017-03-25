package com.inventariumapp.inventarium.Utility;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.inventariumapp.inventarium.R;


/**
 * Created by Yousef on 3/24/2017.
 */

public class ItemHandler {

    // Reference to the fragment
    // Either "shoppingList" or "pantry
    private String listName;

    private RecyclerView mRecyclerView;
    private Activity activity;

    public ItemHandler(RecyclerView recyclerView, Activity activity, String listName) {
        mRecyclerView = recyclerView;
        this.activity = activity;
        this.listName = listName;
    }

    // Implements swipe features
    public void setUpItemTouchHelper() {

        // Swipe Left
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // We want to cache these and not allocate anything repeatedly in the onChildDraw method
            private Drawable background;
            private Drawable icon;
            private int iconMargin;
            private boolean initiatedD;
            private boolean initiatedM;

            // Swipe Left to delete
            private void initSwipeToDelete() {
                background = new ColorDrawable(Color.RED);
                icon = ContextCompat.getDrawable(activity, R.drawable.ic_delete_black_24dp);
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                iconMargin = (int) activity.getResources().getDimension(R.dimen.ic_clear_margin);
                initiatedD = true;
                initiatedM = false;
            }

            // Swipe Right to move
            private void initSwipeToMove() {
                background = new ColorDrawable(Color.GREEN);
                icon = ContextCompat.getDrawable(activity, R.drawable.ic_add_shopping_cart_black_24dp);
                icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                iconMargin = (int) activity.getResources().getDimension(R.dimen.ic_clear_margin);
                initiatedM = true;
                initiatedD = false;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ItemAdapter itemAdapter = (ItemAdapter) recyclerView.getAdapter();
                if (itemAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ItemAdapter adapter = (ItemAdapter) mRecyclerView.getAdapter();

                if (listName.equals("pantry")) {
                    if (swipeDir == ItemTouchHelper.LEFT) {
                        initSwipeToDelete();
                        adapter.setSwipeDir(SwipeDir.LEFT);
                        adapter.addToPendingRemoval(swipedPosition, false);
                    }
                    if (swipeDir == ItemTouchHelper.RIGHT) {
                        initSwipeToMove();
                        adapter.setSwipeDir(SwipeDir.RIGHT);
                        adapter.addToPendingRemoval(swipedPosition, true);
                    }
                } else { // shoppingList
                    if (swipeDir == ItemTouchHelper.LEFT) {
                        initSwipeToMove();
                        adapter.setSwipeDir(SwipeDir.LEFT);
                        adapter.addToPendingRemoval(swipedPosition, true);
                    }
                    if (swipeDir == ItemTouchHelper.RIGHT) {
                        initSwipeToDelete();
                        adapter.setSwipeDir(SwipeDir.RIGHT);
                        adapter.addToPendingRemoval(swipedPosition, false);
                    }
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (dX > 0) { // swiping right

                    if (listName.equals("pantry")) {
                        if (!initiatedM) {
                            initSwipeToMove();
                        }
                        swipeToMove(c, recyclerView, viewHolder, itemView, dX, dY, actionState, isCurrentlyActive);
                    } else {
                        if (!initiatedD) {
                            initSwipeToDelete();
                        }
                        swipeToDelete(c, recyclerView, viewHolder, itemView, dX, dY, actionState, isCurrentlyActive);
                    }
                } else { // swiping left
                    if (listName.equals("shoppingList")) {
                        if (!initiatedM) {
                            initSwipeToMove();
                        }
                        swipeToMove(c, recyclerView, viewHolder, itemView, dX, dY, actionState, isCurrentlyActive);
                    } else {
                        if (!initiatedD) {
                            initSwipeToDelete();
                        }
                        swipeToDelete(c, recyclerView, viewHolder, itemView, dX, dY, actionState, isCurrentlyActive);
                    }
                }
            }

            // Paints swipe green and draws icon
            private void swipeToMove(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, View itemView, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // draw green background
                if (listName.equals("shoppingList")) {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }
                else {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                }
                background.draw(c);

                // draw icon
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = icon.getIntrinsicWidth();
                int intrinsicHeight = icon.getIntrinsicWidth();

                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconBottom = iconTop + intrinsicHeight;
                int iconLeft;
                int iconRight;
                if (listName.equals("shoppingList")) {
                    iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                    iconRight = itemView.getRight() - iconMargin;
                } else {
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                }

                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            // Paints swipe red and draws icon
            private void swipeToDelete(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, View itemView, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // draw red background
                if (listName.equals("shoppingList")) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                }
                else {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                }
                background.draw(c);

                // draw icon
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = icon.getIntrinsicWidth();
                int intrinsicHeight = icon.getIntrinsicWidth();

                int iconLeft;
                int iconRight;
                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconBottom = iconTop + intrinsicHeight;

                if (listName.equals("shoppingList")) {
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                }
                else {
                    iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                    iconRight = itemView.getRight() - iconMargin;
                }
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

            ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        }

    // Translates rows vertically
    public void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background = new ColorDrawable(Color.WHITE);

            private void init() {
                ItemAdapter adapter = (ItemAdapter) mRecyclerView.getAdapter();

                if (adapter.getSwipeDir() == SwipeDir.RIGHT) {
                    if (listName.equals("shoppingList")) {background = new ColorDrawable(Color.RED); }
                    else {background = new ColorDrawable(Color.GREEN); }
                }
                else if (adapter.getSwipeDir() == SwipeDir.LEFT) {
                    if (listName.equals("shoppingList")) {background = new ColorDrawable(Color.GREEN); }
                    else {background = new ColorDrawable(Color.RED); }
                }
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                init();

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
}
