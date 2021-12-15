package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ISelectableRecyclerItem;

import java.util.List;

public class SelectIfNotSelectedSelectionRule implements SelectionRules {
    @Override
    public boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        if (!(item instanceof ISelectableRecyclerItem)) {
            return false;
        }

        return !otherSelectedItems.contains(item);
    }
}
