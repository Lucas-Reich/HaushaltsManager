package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdItem implements IRecyclerItem {
    private static final String TAG = AdItem.class.getSimpleName();

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

    public Calendar getDate() {
        // TODO: Was soll ich als Datum zurückgeben
        return null;
    }
}
