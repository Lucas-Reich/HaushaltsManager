package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.List;

public class MockInsertStrategy implements InsertStrategy {
    @Override
    public int insert(IRecyclerItem item, List<IRecyclerItem> items) {
        items.add(item);
        return items.size();
    }
}
