package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildItem implements IRecyclerItem {
    private static final String TAG = ChildItem.class.getSimpleName();

    public static final int VIEW_TYPE = 4;
    private ExpenseObject mExpense;
    private long mParentId;

    public ChildItem(ExpenseObject expense, long parentId) {
        mExpense = expense;
        mParentId = parentId;
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
    public Calendar getDate() {
        return mExpense.getDateTime();
    }

    @Deprecated
    // Kann ich die Methode Deprecaten? Sie wird nur für die RevertExpenseDeletionSnackbar benötigt
    public long getParentId() {
        return mParentId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChildItem)) {
            return false;
        }

        ChildItem other = (ChildItem) obj;

        return other.getContent().equals(getContent());
    }
}
