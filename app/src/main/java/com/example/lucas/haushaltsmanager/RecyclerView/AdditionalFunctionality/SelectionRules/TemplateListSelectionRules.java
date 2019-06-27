package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.List;

public class TemplateListSelectionRules implements SelectionRules {
    @Override
    public boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> items) {
        return false;
    }
}
