package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public class CategoryListSelectionRules implements SelectionRules {
    @Override
    public boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        if (!item.isSelectable()) {
            return false;
        }

        return !otherSelectedItems.contains(item);
    }
}
