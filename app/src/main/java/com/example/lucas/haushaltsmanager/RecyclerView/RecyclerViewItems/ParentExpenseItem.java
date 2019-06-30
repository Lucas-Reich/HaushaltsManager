package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class ParentExpenseItem implements IParentRecyclerItem {
    public static final int VIEW_TYPE = 2;

    private ParentExpenseObject expense;
    private boolean mIsExpanded = false;
    private List<IRecyclerItem> children;
    private DateItem parent;

    public ParentExpenseItem(ParentExpenseObject parentExpense, DateItem parent) {
        expense = parentExpense;
        children = createChildItems(parentExpense.getChildren());

        this.parent = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public ParentExpenseObject getContent() {
        return expense;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return children;
    }

    @Override
    public DateItem getParent() {
        return parent;
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
        if (children.contains(child)) {
            return;
        }

        children.add(child);
    }

    @Override
    public void removeChild(IRecyclerItem child) {
        if (!children.contains(child)) {
            return;
        }

        children.remove(child);
        expense.getChildren().remove(child.getContent());
    }

    private List<IRecyclerItem> createChildItems(List<ExpenseObject> children) {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (ExpenseObject child : children) {
            childItems.add(new ChildExpenseItem(child, this));
        }

        return childItems;
    }
}
