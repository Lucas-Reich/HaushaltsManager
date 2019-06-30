package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.List;

public class RecurringBookingItem implements IParentRecyclerItem {
    public static final int VIEW_TYPE = 5;

    private RecurringBooking recurringBooking;
    private List<IRecyclerItem> children;
    private boolean mIsExpanded;

    public RecurringBookingItem(RecurringBooking recurringBooking) {
        this.recurringBooking = recurringBooking;
        mIsExpanded = false;

        createChildren(recurringBooking.getBooking().getChildren());
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
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public void removeChild(IRecyclerItem item) {
        // Do nothing
    }

    private void createChildren(List<ExpenseObject> children) {
        this.children = new ArrayList<>();

        for (ExpenseObject child : children) {
            this.children.add(new ChildExpenseItem(child, this));
        }
    }
}
