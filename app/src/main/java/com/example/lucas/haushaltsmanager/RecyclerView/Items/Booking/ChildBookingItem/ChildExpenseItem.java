package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.IBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;

public class ChildExpenseItem implements IBookingItem {
    public static final int VIEW_TYPE = 4;

    private IParentRecyclerItem parent;
    private Booking expense;

    public ChildExpenseItem(Booking expense, IParentRecyclerItem parent) {
        this.expense = expense;
        this.parent = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Booking getContent() {
        return expense;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChildExpenseItem)) {
            return false;
        }

        ChildExpenseItem other = (ChildExpenseItem) obj;

        return other.getContent().equals(getContent());
    }

    @NonNull
    @Override
    public String toString() {
        return expense.toString();
    }

    @Override
    public IParentRecyclerItem getParent() {
        return parent;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }
}
