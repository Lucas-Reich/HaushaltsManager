package com.example.lucas.haushaltsmanager.FABToolbar.Actions;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public class ActionPayload {
    private List<IRecyclerItem> items = new ArrayList<>();

    public void setPayload(List<IRecyclerItem> items) {
        this.items.addAll(items);
    }

    public List<IRecyclerItem> getItems() {
        return items;
    }

    public IRecyclerItem getFirstItem() {
        return items.get(0);
    }
}
