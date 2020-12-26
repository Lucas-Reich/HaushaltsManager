package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryItem;

import java.util.ArrayList;
import java.util.List;

public class CreateCategoryItemsStrategy implements RecyclerItemCreatorStrategyInterface<Category> {
    @Override
    public List<IRecyclerItem> create(List<Category> categories) {
        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> categoryItems = new ArrayList<>();
        for (Category category : categories) {
            categoryItems.add(new ParentCategoryItem(category));
        }

        return categoryItems;
    }
}
