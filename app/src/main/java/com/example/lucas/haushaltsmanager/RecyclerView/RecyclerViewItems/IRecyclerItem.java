package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import java.util.List;

public interface IRecyclerItem {
    int getViewType();

    Object getContent();

    boolean canExpand();

    boolean isExpanded();

    void setExpanded(boolean isExpanded);

    IRecyclerItem getParent();

    boolean equals(Object obj);

    // TODO: Sollte ich noch ein SubInterface erstellen, welches für ParentItems genutzt werden kann
    //  und folgende Methoden enthält:

    List<IRecyclerItem> getChildren();

    void addChild(IRecyclerItem item);

    // void removeChild(IRecyclerItem item);
}
