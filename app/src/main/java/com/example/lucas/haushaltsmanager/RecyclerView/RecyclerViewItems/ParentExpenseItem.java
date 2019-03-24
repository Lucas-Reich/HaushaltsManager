package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ParentExpenseItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 2;

    private ParentExpenseObject mParentExpense;
    private boolean mIsExpanded = false;
    private List<IRecyclerItem> mChildItems;

    public ParentExpenseItem(ParentExpenseObject parentExpense) {
        mParentExpense = parentExpense;
        mChildItems = createChildItems(parentExpense.getChildren());
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ParentExpenseObject getContent() {
        return mParentExpense;
    }

    @Override
    public boolean canExpand() {
        return true;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return mChildItems;
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    public Calendar getDate() {
        return mParentExpense.getDate();
    }

    public boolean hasChild(ExpenseObject expense) {
        return mParentExpense.getChildren().contains(expense);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParentExpenseItem)) {
            return false;
        }

        ParentExpenseItem other = (ParentExpenseItem) obj;

        return other.getContent().equals(getContent())
                && other.getChildren().equals(getChildren());
    }

    private List<IRecyclerItem> createChildItems(List<ExpenseObject> children) {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (ExpenseObject child : children) {
            childItems.add(new ChildItem(child, mParentExpense.getParent().getIndex()));
        }

        return childItems;
    }
}
