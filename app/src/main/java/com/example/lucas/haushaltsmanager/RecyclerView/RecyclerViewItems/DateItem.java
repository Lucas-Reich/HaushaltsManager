package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateItem implements IParentRecyclerItem {
    public static final int VIEW_TYPE = 0;

    private Calendar date;

    public DateItem(Calendar date) {
        this.date = date;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Calendar getContent() {
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return CalendarUtils.formatHumanReadable(getContent());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateItem)) {
            return false;
        }

        DateItem other = (DateItem) obj;

        return other.getContent() == getContent();

    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public void removeChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public boolean isExpanded() {
        // DateItem is always expanded and cannot be collapsed
        return true;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        // DateItem is always expanded and cannot be collapsed
    }
}
