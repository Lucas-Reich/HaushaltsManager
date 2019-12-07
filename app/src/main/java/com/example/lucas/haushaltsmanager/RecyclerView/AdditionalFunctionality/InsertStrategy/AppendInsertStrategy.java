package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.List;

/**
 * Neue Items werden immer an das Ende der Liste angehängt
 */
public class AppendInsertStrategy implements InsertStrategy {
    @Override
    public int insert(IRecyclerItem item, List<IRecyclerItem> items) {
        items.add(item);

        return items.size() - 1;
    }
}
