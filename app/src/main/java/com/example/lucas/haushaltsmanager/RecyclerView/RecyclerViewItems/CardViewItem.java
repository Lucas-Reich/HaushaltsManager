package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import android.support.annotation.DrawableRes;

public class CardViewItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 9;

    @DrawableRes
    private final int imageRes;

    public CardViewItem(@DrawableRes int imageRes) {
        this.imageRes = imageRes;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    @DrawableRes
    public Integer getContent() {
        return imageRes;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }
}
