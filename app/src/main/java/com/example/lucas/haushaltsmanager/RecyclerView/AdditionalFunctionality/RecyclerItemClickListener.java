package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

/**
 * Tutorial: https://stackoverflow.com/a/26826692
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnRecyclerItemClickListener mListener;
    private final GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerItemClickListener listener) {
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

                    mListener.onLongClick(childView, getItem(recyclerView, position), position);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, MotionEvent e) {
        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            int position = getPosition(recyclerView, childView);

            mListener.onClick(childView, getItem(recyclerView, position), position);
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

    public interface OnRecyclerItemClickListener {
        void onClick(View v, IRecyclerItem item, int position);

        void onLongClick(View v, IRecyclerItem item, int position);
    }
}
