package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class ParentExpenseItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 2;

    private ParentExpenseObject mParentExpense;
    private boolean mIsExpanded = false;
    private List<IRecyclerItem> mChildItems;
    private DateItem mParent;

    public ParentExpenseItem(ParentExpenseObject parentExpense, DateItem parent) {
        mParentExpense = parentExpense;
        mChildItems = createChildItems(parentExpense.getChildren());

        mParent = parent;
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

    @Override
    public DateItem getParent() {
        return mParent;
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
        if (mChildItems.contains(child)) {
            return;
        }

        mChildItems.add(child);
    }

    public void removeChild(IRecyclerItem child) {
        if (!mChildItems.contains(child)) {
            return;
        }

        mChildItems.remove(child);
        mParentExpense.getChildren().remove(child.getContent());
    }

    private List<IRecyclerItem> createChildItems(List<ExpenseObject> children) {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (ExpenseObject child : children) {
            childItems.add(new ChildExpenseItem(child, this));
        }

        return childItems;
    }
}
