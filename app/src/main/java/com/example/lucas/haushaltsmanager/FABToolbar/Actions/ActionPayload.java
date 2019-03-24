package com.example.lucas.haushaltsmanager.FABToolbar.Actions;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectedRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public class ActionPayload {
    private List<SelectedRecyclerItem> mList = new ArrayList<>();

    public void setPayload(List<SelectedRecyclerItem> items) {
        mList.addAll(items);
    }

    public List<SelectedRecyclerItem> getItems() {
        return mList;
    }

    public SelectedRecyclerItem getItem() {
        return mList.get(0);
    }
}
