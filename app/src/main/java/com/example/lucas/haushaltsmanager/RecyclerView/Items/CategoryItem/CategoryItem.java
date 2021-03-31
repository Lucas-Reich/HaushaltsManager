package com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public class CategoryItem implements IRecyclerItem {
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
    public IParentRecyclerItem getParent() {
        return null;
    }

    public boolean isSelectable() {
        return true;
    }
}
