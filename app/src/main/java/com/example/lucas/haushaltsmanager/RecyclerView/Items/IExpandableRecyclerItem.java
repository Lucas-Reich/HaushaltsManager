package com.example.lucas.haushaltsmanager.RecyclerView.Items;

import java.util.List;

public interface IExpandableRecyclerItem extends IRecyclerItem {

    List<IRecyclerItem> getChildren();

    void addChild(IRecyclerItem item);

    void removeChild(IRecyclerItem item);

    boolean isExpanded();

    void setExpanded(boolean isExpanded);
}
