package com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem;

import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public class RecurringBookingItem implements IParentRecyclerItem {
    public static final int VIEW_TYPE = 5;

    private final RecurringBooking recurringBooking;
    private List<IRecyclerItem> children;
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
        return children;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public void removeChild(IRecyclerItem item) {
        // Do nothing
    }

    private void createChildren(List<Booking> children) {
        this.children = new ArrayList<>();

        for (Booking child : children) {
            this.children.add(new ChildExpenseItem(child, this));
        }
    }
}
