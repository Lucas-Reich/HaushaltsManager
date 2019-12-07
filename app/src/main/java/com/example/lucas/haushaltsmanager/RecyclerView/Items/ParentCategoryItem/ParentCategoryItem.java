package com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildCategoryItem.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public class ParentCategoryItem implements IParentRecyclerItem {
    public static final int VIEW_TYPE = 7;

    private Category category;
    private List<IRecyclerItem> children;
    private boolean mIsExpanded = false;

    public ParentCategoryItem(Category category) {
        this.category = category;

        children = createChildItems(category.getChildren());
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
    public boolean isExpanded() {
        return mIsExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return children;
    }

    @Override
    public void addChild(IRecyclerItem item) {
        if (children.contains(item)) {
            return;
        }

        children.add(item);
    }

    @Override
    public void removeChild(IRecyclerItem item) {

    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    private List<IRecyclerItem> createChildItems(List<Category> children) {
        List<IRecyclerItem> childItems = new ArrayList<>();

        for (Category category : children) {
            childItems.add(new ChildCategoryItem(category, this));
        }

        return childItems;
    }
}
