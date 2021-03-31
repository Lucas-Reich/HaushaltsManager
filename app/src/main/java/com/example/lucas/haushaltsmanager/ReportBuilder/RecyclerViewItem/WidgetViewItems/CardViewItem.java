package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public abstract class CardViewItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 9;

    private final CardViewContent content;

    public CardViewItem(CardViewContent content) {
        this.content = content;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public CardViewContent getContent() {
        return content;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CardViewItem)) {
            return false;
        }

        CardViewItem other = (CardViewItem) obj;

        return other.getContent().equals(content);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }
}
