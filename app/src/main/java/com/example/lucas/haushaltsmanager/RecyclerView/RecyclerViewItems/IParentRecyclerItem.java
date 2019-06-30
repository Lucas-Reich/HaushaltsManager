package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import java.util.List;

public interface IParentRecyclerItem extends IRecyclerItem {

    List<IRecyclerItem> getChildren();

    void addChild(IRecyclerItem item);

    void removeChild(IRecyclerItem item);

    boolean isExpanded();

    void setExpanded(boolean isExpanded);
}
