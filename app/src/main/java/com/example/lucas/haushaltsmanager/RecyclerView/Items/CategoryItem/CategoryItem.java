package com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.ISelectableRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;

public class CategoryItem implements ISelectableRecyclerItem {
    public static final int VIEW_TYPE = 7;

    private final Category category;

    public CategoryItem(Category category) {
        this.category = category;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Category getContent() {
        return category;
    }

    @Override
    public IExpandableRecyclerItem getParent() {
        return null;
    }
}
