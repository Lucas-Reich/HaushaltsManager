package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewEmptyObserver extends RecyclerView.AdapterDataObserver {
    private View mEmptyView;
    private RecyclerView mRecyclerView;

    public RecyclerViewEmptyObserver(RecyclerView recyclerView, View emptyView, int initialCount) {
        mRecyclerView = recyclerView;
        mEmptyView = emptyView;

        if (initialCount == 0) {
            showEmptyView();
        }
    }

    public void onChanged() {
        super.onChanged();
        checkIfEmpty();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        checkIfEmpty();
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (isListEmpty()) {
            showEmptyView();
        } else {
            showRecyclerView();
        }
    }

    private boolean isListEmpty() {
        if (null == mRecyclerView.getAdapter())
            return true;

        return mRecyclerView.getAdapter().getItemCount() == 0;
    }

    private void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }
}
