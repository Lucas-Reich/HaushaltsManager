package com.example.lucas.haushaltsmanager;

import android.support.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class ExpListViewSelectedItem {
    private ExpenseObject mSelectedItem;
    private ExpenseObject mItemParent;

    public ExpListViewSelectedItem(ExpenseObject item, @Nullable ExpenseObject parent) {
        mSelectedItem = item;
        mItemParent = parent;
    }

    public boolean isParent() {
        return mItemParent == null;
    }

    public ExpenseObject getParent() {
        return mItemParent;
    }

    public ExpenseObject getItem() {
        return mSelectedItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExpListViewSelectedItem))
            return false;

        ExpListViewSelectedItem otherItem = (ExpListViewSelectedItem) obj;

        boolean equals = otherItem.isParent() == isParent();
        if (otherItem.getParent() == null)
            equals = equals && (getParent() == null);
        else
            equals = equals && otherItem.getParent().equals(getParent());
        equals = equals && otherItem.getItem().equals(getItem());

        return equals;
    }
}
