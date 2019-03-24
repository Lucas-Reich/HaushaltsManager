package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import java.util.Calendar;
import java.util.List;

public interface IRecyclerItem {
    int getViewType();

    Object getContent();

    boolean canExpand();

    boolean isExpanded();

    void setExpanded(boolean isExpanded);

    List<IRecyclerItem> getChildren();

    Calendar getDate();
}
