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
import android.util.Log;
import android.view.View;

import com.inventariumapp.inventarium.R;

import static android.R.drawable.ic_input_add;

/**
 * Created by Yousef on 3/20/2017.
 */

public class ItemHandler {

    RecyclerView mRecyclerView;
    Activity activity;

    public ItemHandler(RecyclerView recyclerView, Activity activity) {
        mRecyclerView = recyclerView;
        this.activity = activity;
    }

    // Implements swipe features
    public void setUpItemTouchHelper() {

        // Swipe Left
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiatedL;
            boolean initiatedR;

            // Swipe Left to delete!
            private void initLeftSwipe() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(activity, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) activity.getResources().getDimension(R.dimen.ic_clear_margin);
                initiatedL = true;
                initiatedR = false;
            }

            // Swipe Right to move
            private void initRightSwipe() {
                background = new ColorDrawable(Color.GREEN);
                xMark = ContextCompat.getDrawable(activity, ic_input_add);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) activity.getResources().getDimension(R.dimen.ic_clear_margin);
                initiatedR = true;
                initiatedL = false;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ItemAdapter itemAdapter = (ItemAdapter)recyclerView.getAdapter();
                if (itemAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ItemAdapter adapter = (ItemAdapter)mRecyclerView.getAdapter();
                if (swipeDir == ItemTouchHelper.LEFT) {
                    initLeftSwipe();
                    adapter.swipeDir = ItemAdapter.SwipeDir.LEFT;
                    adapter.addToPendingRemoval(swipedPosition);
                }
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    initRightSwipe();
                    adapter.swipeDir = ItemAdapter.SwipeDir.RIGHT;
                    adapter.addToPendingRemoval(swipedPosition);
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

                if (dX > 0) { // swiping right to move

                    if (!initiatedR) {
                        initRightSwipe();
                    }

                    // draw red background
                    background.setBounds(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
                    background.draw(c);

                    // draw + mark
                    int itemHeight = itemView.getBottom() - itemView.getTop();
                    int intrinsicWidth = xMark.getIntrinsicWidth();
                    int intrinsicHeight = xMark.getIntrinsicWidth();

                    int xMarkLeft = itemView.getLeft() + xMarkMargin ;
                    int xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;

                    int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                    int xMarkBottom = xMarkTop + intrinsicHeight;
                    xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                    xMark.draw(c);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                else {// swiping left to delete

                    if (!initiatedL) {
                        initLeftSwipe();
                    }

                    // draw green background
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    background.draw(c);

                    // draw x mark
                    int itemHeight = itemView.getBottom() - itemView.getTop();
                    int intrinsicWidth = xMark.getIntrinsicWidth();
                    int intrinsicHeight = xMark.getIntrinsicWidth();

                    int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                    int xMarkRight = itemView.getRight() - xMarkMargin;
                    int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                    int xMarkBottom = xMarkTop + intrinsicHeight;
                    xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                    xMark.draw(c);

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;

            private void init() {
                ItemAdapter adapter = (ItemAdapter)mRecyclerView.getAdapter();

                if (adapter.swipeDir == ItemAdapter.SwipeDir.RIGHT) {
                    background = new ColorDrawable(Color.GREEN);
                }
                else if (adapter.swipeDir == ItemAdapter.SwipeDir.LEFT) {
                    background = new ColorDrawable(Color.RED);
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
