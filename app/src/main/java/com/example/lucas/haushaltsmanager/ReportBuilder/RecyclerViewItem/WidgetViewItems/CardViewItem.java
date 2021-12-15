package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

public abstract class CardViewItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 9;

    private final Widget widget;

    public CardViewItem(Widget widget) {
        this.widget = widget;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Widget getContent() {
        return widget;
    }

    @Override
    public IExpandableRecyclerItem getParent() {
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

        return other.getContent().equals(widget);
    }

    @Override
    public boolean isSelectable() {
        return false;
    }
}
