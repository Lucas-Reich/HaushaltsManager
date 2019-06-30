package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Entities.Category;

public class ChildCategoryItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 6;

    private ParentCategoryItem parent;
    private Category category;

    public ChildCategoryItem(Category category, ParentCategoryItem parent) {
        this.category = category;
        this.parent = parent;
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
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChildCategoryItem)) {
            return false;
        }

        ChildCategoryItem other = (ChildCategoryItem) obj;

        return other.getContent().equals(getContent());
    }

    @NonNull
    @Override
    public String toString() {
        return category.toString();
    }

    @Override
    public ParentCategoryItem getParent() {
        return parent;
    }
}
