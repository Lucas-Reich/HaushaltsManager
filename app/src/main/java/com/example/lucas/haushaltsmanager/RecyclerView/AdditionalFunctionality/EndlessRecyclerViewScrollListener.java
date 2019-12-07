package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Tutorial: https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView#implementing-with-recyclerview
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final int VISIBLE_THRESHOLD = 10;
    private static final int STARTING_PAGE_INDEX = 0;

    private int mCurrentPage = 0;
    private int mCurrentItemCount = 0;
    private boolean mIsLoading = true;
    private RecyclerView.LayoutManager mLayoutManager;

    protected EndlessRecyclerViewScrollListener(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        int lastVisibleItemPosition = getLastVisibleItemPosition();
        int totalItemCount = mLayoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < mCurrentItemCount) {
            mCurrentPage = STARTING_PAGE_INDEX;
            mCurrentItemCount = totalItemCount;
            if (totalItemCount == 0) {
                mIsLoading = true;
            }
        }
        // If it’s still loading, we check to see if the data set count has
        // changed, if so we conclude it has finished mIsLoading and update the current page
        // number and total item count.
        if (isLoadingFinished(totalItemCount)) {
            mIsLoading = false;
            mCurrentItemCount = totalItemCount;
        }

        if (belowVisibleThreshold(lastVisibleItemPosition, totalItemCount)) {
            mCurrentPage++;

            onLoadMore(mCurrentPage, totalItemCount, recyclerView);
            mIsLoading = true;
        }
    }

    public void resetState() {
        mCurrentPage = STARTING_PAGE_INDEX;
        mCurrentItemCount = 0;
        mIsLoading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    private boolean belowVisibleThreshold(int lastVisibleItemPosition, int totalItemCount) {
        return !mIsLoading && (lastVisibleItemPosition + VISIBLE_THRESHOLD) > totalItemCount;
    }

    private boolean isLoadingFinished(int totalItemCount) {
        return mIsLoading && (totalItemCount > mCurrentItemCount);
    }

    private int getLastVisibleItemPosition() {
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
            // get maximum element within the list
            return getLastVisibleItem(lastVisibleItemPositions);
        }

        if (mLayoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        if (mLayoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        }

        return 0;
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }
}