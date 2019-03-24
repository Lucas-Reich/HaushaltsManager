package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

public class SelectedRecyclerItem {
    private int mPosition;
    private IRecyclerItem mItem;

    SelectedRecyclerItem(IRecyclerItem item, int position) {
        this.mPosition = position;
        this.mItem = item;
    }

    public IRecyclerItem getItem() {
        return mItem;
    }

    public int getPosition() {
        return mPosition;
    }
}
