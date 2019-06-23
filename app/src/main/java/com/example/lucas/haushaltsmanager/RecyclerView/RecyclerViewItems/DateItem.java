package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateItem implements IRecyclerItem {
    public static final String TAG = DateItem.class.getSimpleName();

    public static final int VIEW_TYPE = 0;
    private Calendar mDate;

    public DateItem(Calendar date) {
        mDate = date;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Calendar getContent() {
        return mDate;
    }

    @Override
    public boolean canExpand() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return true;
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
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public IRecyclerItem getParent() {
        return null;
    }
}
