package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class ChildCategoryItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 6;
    private static final String TAG = ChildCategoryItem.class.getSimpleName();

    private Category mCategory;
    private ParentCategoryItem mParentCategory;

    public ChildCategoryItem(Category category, ParentCategoryItem parent) {
        mCategory = category;
        mParentCategory = parent;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Category getContent() {
        return mCategory;
    }

    @Override
    public boolean canExpand() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        throw new IllegalStateException(String.format(
                "setExpanded method called on a Object that cannot expand: %s",
                TAG
        ));
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChildCategoryItem)) {
            return false;
        }

        ChildCategoryItem other = (ChildCategoryItem) obj;

        return other.getContent().equals(getContent());
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
    }

    @Override
    public ParentCategoryItem getParent() {
        return mParentCategory;
    }
}
