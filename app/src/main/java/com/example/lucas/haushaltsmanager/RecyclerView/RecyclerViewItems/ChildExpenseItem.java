package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public class ChildExpenseItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 4;

    private IParentRecyclerItem parent;
    private ExpenseObject expense;

    public ChildExpenseItem(ExpenseObject expense, IParentRecyclerItem parent) {
        this.expense = expense;
        this.parent = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ExpenseObject getContent() {
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
}
