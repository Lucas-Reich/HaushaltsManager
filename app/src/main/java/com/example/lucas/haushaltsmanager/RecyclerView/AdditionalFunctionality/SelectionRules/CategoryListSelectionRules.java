package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentCategoryItem;

import java.util.List;

public class CategoryListSelectionRules implements SelectionRules {
    @Override
    public boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        if (hasWrongClass(item)) {
            return false;
        }

        if (hasDifferentParent(item, otherSelectedItems)) {
            return false;
        }

        if (isAlreadySelected(item, otherSelectedItems)) {
            return false;
        }

        return true;
    }

    private boolean hasDifferentParent(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        for (IRecyclerItem selectedItem : otherSelectedItems) {
            if (itemsHaveSameParent(item, selectedItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAlreadySelected(IRecyclerItem item, List<IRecyclerItem> selectedItems) {
        return selectedItems.contains(item);
    }

    private boolean itemsHaveSameParent(IRecyclerItem item, IRecyclerItem other) {
        return item.getParent().equals(other.getParent());
    }

    private boolean hasWrongClass(IRecyclerItem item) {
        if (item instanceof ParentCategoryItem) {
            return true;
        }

        if (item instanceof AdItem) {
            return true;
        }

        return false;
    }
}
