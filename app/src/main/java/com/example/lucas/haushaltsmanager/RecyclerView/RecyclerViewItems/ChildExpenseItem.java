package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class ChildExpenseItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 4;
    private static final String TAG = ChildExpenseItem.class.getSimpleName();

    private ExpenseObject mExpense;
    private IRecyclerItem mParent;

    public ChildExpenseItem(ExpenseObject expense, IRecyclerItem parent) {
        mExpense = expense;
        mParent = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ExpenseObject getContent() {
        return mExpense;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChildExpenseItem)) {
            return false;
        }

        ChildExpenseItem other = (ChildExpenseItem) obj;

        return other.getContent().equals(getContent());
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public IRecyclerItem getParent() {
        return mParent;
    }
}
