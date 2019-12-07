package com.example.lucas.haushaltsmanager.RecyclerView.Items.AdItem;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public class AdItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 3;

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Object getContent() {
        return null;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AdItem)) {
            return false;
        }

        return false;
    }
}
