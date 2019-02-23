package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

// REFACTORING: Ich kann die Selection implementierung hiermit https://proandroiddev.com/a-guide-to-recyclerview-selection-3ed9f2381504 austauschen
public abstract class RecyclerViewSelectedItemHandler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SparseArray<SelectedRecyclerItem> mSelectedItems;

    RecyclerViewSelectedItemHandler() {
        mSelectedItems = new SparseArray<>();
    }

    public void toggleSelection(IRecyclerItem item, int itemPosition) {
        if (isItemSelected(itemPosition)) {
            removeItem(itemPosition);
        } else {
            addItem(item, itemPosition);
        }

        notifyItemChanged(itemPosition);
    }

    public void clearSelections() {
        if (mSelectedItems.size() > 0) {
            mSelectedItems.clear();
            notifyDataSetChanged();
        }
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public int getSelectedItemsCount() {
        int itemCount = 0;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            SelectedRecyclerItem item = mSelectedItems.get(mSelectedItems.keyAt(i));

            if (!(item.getItem() instanceof ChildItem)) {
                itemCount++;
            }
        }

        return itemCount;
    }

    public int getSelectedChildCount() {
        int childCount = 0;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            SelectedRecyclerItem item = mSelectedItems.get(mSelectedItems.keyAt(i));

            if (item.getItem() instanceof ChildItem) {
                childCount++;
            }
        }

        return childCount;
    }

    public List<SelectedRecyclerItem> getSelectedItems() {
        List<SelectedRecyclerItem> selectedItems = new ArrayList<>();

        for (int i = 0; i < mSelectedItems.size(); i++) {
            selectedItems.add(mSelectedItems.get(mSelectedItems.keyAt(i)));
        }

        return selectedItems;
    }

    protected boolean isItemSelected(int position) {
        return mSelectedItems.get(position) != null;
    }

    private void removeItem(int position) {
        mSelectedItems.remove(position);
    }

    private void addItem(IRecyclerItem item, int position) {
        mSelectedItems.put(position, new SelectedRecyclerItem(item, position));
    }
}
