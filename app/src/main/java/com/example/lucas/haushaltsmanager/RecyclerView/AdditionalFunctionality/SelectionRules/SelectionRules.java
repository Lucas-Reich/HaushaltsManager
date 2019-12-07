package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public interface SelectionRules {
    boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> items);
}
