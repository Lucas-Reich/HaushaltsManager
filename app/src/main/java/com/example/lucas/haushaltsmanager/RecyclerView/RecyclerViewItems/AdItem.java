package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import java.util.ArrayList;
import java.util.List;

public class AdItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 3;
    private static final String TAG = AdItem.class.getSimpleName();

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Object getContent() {
        return null;
    }

    @Override
    public boolean canExpand() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        throw new IllegalStateException(String.format("setExpanded method called on a Object that cannot expand: %s", TAG));
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public IRecyclerItem getParent() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AdItem)) {
            return false;
        }

        return false;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }
}
