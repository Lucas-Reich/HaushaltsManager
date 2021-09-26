package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.IBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Booking.ParentBooking;

import java.util.ArrayList;
import java.util.List;

public class ParentBookingItem implements IParentRecyclerItem, IBookingItem {
    public static final int VIEW_TYPE = 2;

    private final ParentBooking booking;
    private boolean mIsExpanded = false;
    private final DateItem parent;

    public ParentBookingItem(
            @NonNull ParentBooking booking,
            @NonNull DateItem parent
    ) {
        this.booking = booking;
        this.parent = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ParentBooking getContent() {
        return booking;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (Booking child : booking.getChildren()) {
            childItems.add(new ChildExpenseItem(child, this));
        }

        return childItems;
    }

    @Override
    public DateItem getParent() {
        return parent;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentBookingItem)) {
            return false;
        }

        ParentBookingItem other = (ParentBookingItem) obj;

        return other.getContent().equals(getContent())
                && other.getChildren().equals(getChildren());
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
    public void addChild(IRecyclerItem child) {
        booking.addChild((Booking) child.getContent());
    }

    @Override
    public void removeChild(IRecyclerItem child) {
        booking.getChildren().remove((Booking) child.getContent());
    }
}
