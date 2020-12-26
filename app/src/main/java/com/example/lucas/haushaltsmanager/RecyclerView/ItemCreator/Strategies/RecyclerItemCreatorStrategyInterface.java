package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public interface RecyclerItemCreatorStrategyInterface<T> {
    List<IRecyclerItem> create(List<T> objects);
}
