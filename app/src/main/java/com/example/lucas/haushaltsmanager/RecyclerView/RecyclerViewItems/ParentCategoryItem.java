package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class ParentCategoryItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 7;

    private Category mParentCategory;
    private List<IRecyclerItem> mChildren;
    private boolean mIsExpanded = false;

    public ParentCategoryItem(Category category) {
        mParentCategory = category;

        mChildren = createChildItems(category.getChildren());
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Category getContent() {
        return mParentCategory;
    }

    @Override
    public boolean canExpand() {
        return true;
    }

    @Override
    public boolean isExpanded() {
        return mIsExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return mChildren;
    }

    @Override
    public IRecyclerItem getParent() {
        return null;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        if (mChildren.contains(item)) {
            return;
        }

        mChildren.add(item);
    }

    private List<IRecyclerItem> createChildItems(List<Category> children) {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (Category category : children) {
            childItems.add(new ChildCategoryItem(category, this));
        }

        return childItems;
    }
}
