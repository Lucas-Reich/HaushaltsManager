package com.example.lucas.haushaltsmanager.RecyclerView.Items;

import androidx.annotation.NonNull;

public interface IRecyclerItem {
    @NonNull
    String toString();

    boolean equals(Object obj);

    int getViewType();

    Object getContent();

    IParentRecyclerItem getParent();

    boolean isSelectable(); // TODO: Replace with ISelectableRecyclerItem interface
}
