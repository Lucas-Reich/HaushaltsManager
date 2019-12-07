package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public interface InsertStrategy {
    int INVALID_INDEX = -1;

    int insert(IRecyclerItem item, List<IRecyclerItem> items);
}
