package com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;

import java.util.ArrayList;
import java.util.List;

public class RecurringBookingItem implements IExpandableRecyclerItem {
    public static final int VIEW_TYPE = 5;

    private final RecurringBooking recurringBooking;
    private boolean mIsExpanded;

    public RecurringBookingItem(RecurringBooking recurringBooking) {
        this.recurringBooking = recurringBooking;
        mIsExpanded = false;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public RecurringBooking getContent() {
        return recurringBooking;
    }

    @Override
    public boolean isExpanded() {
        return mIsExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public IExpandableRecyclerItem getParent() {
        return null;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public void removeChild(IRecyclerItem item) {
        // Do nothing
    }
}
