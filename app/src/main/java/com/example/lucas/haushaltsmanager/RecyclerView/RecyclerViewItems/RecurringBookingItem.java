package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 5;

    private RecurringBooking mRecurringBooking;
    private boolean mIsExpanded;

    public RecurringBookingItem(RecurringBooking recurringBooking) {
        mRecurringBooking = recurringBooking;
        mIsExpanded = false;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Object getContent() {
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
        return transform(mRecurringBooking.getBooking().getChildren());
    }

    @Override
    public Calendar getDate() {
        return mRecurringBooking.getExecutionDate();
    }

    private List<IRecyclerItem> transform(List<ExpenseObject> children) {
        List<IRecyclerItem> items = new ArrayList<>();
        for (ExpenseObject child : children) {
            items.add(new ChildItem(child, mRecurringBooking.getIndex()));
        }

        return items;
    }
}
