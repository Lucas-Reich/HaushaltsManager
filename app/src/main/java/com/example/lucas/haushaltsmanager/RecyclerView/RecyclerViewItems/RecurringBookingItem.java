package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.List;

public class RecurringBookingItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 5;

    private RecurringBooking mRecurringBooking;
    private List<IRecyclerItem> mChildren;
    private boolean mIsExpanded;

    public RecurringBookingItem(RecurringBooking recurringBooking) {
        mRecurringBooking = recurringBooking;
        mIsExpanded = false;

        createChildren(recurringBooking.getBooking().getChildren());
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public RecurringBooking getContent() {
        return mRecurringBooking;
    }

    @Override
    public boolean canExpand() {
        return mRecurringBooking.getBooking().isParent();
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
        return mChildren;
    }

    @Override
    public IRecyclerItem getParent() {
        return null;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    private void createChildren(List<ExpenseObject> children) {
        mChildren = new ArrayList<>();

        for (ExpenseObject child : children) {
            mChildren.add(new ChildExpenseItem(child, this));
        }
    }
}
