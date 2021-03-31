package com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.ReportBuilder.IChart;

public class CardViewItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 10;

    private final IChart chart;

    public CardViewItem(IChart chart) {
        this.chart = chart;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public IChart getContent() {
        return chart;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }
}
