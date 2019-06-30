package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public class ExpenseItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 1;

    private ExpenseObject expense;
    private DateItem parent;

    public ExpenseItem(ExpenseObject expense, DateItem parent) {
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
        if (!(obj instanceof ExpenseItem)) {
            return false;
        }

        ExpenseItem other = (ExpenseItem) obj;

        return other.getContent().equals(getContent());
    }

    @NonNull
    @Override
    public String toString() {
        return expense.toString();
    }

    @Override
    public DateItem getParent() {
        return parent;
    }
}
