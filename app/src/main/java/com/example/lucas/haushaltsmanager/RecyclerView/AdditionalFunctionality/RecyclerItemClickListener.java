package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

/**
 * Tutorial: https://stackoverflow.com/a/26826692
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mListener != null) {
                    int position = getPosition(recyclerView, childView);

                    mListener.onItemLongClick(getItem(recyclerView, position), position);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, MotionEvent e) {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            int position = getPosition(recyclerView, childView);

            mListener.onItemClick(getItem(recyclerView, position), position);
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private IRecyclerItem getItem(RecyclerView recyclerView, int position) {
        return ((RecyclerViewItemHandler) recyclerView.getAdapter()).getItem(position);
    }

    private int getPosition(RecyclerView recyclerView, View childView) {
        return recyclerView.getChildAdapterPosition(childView);
    }

    public interface OnItemClickListener {
        void onItemClick(IRecyclerItem item, int position);

        void onItemLongClick(IRecyclerItem item, int position);
    }
}
