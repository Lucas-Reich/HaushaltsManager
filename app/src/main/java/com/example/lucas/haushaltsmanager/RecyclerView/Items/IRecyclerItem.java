package com.example.lucas.haushaltsmanager.RecyclerView.Items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IRecyclerItem {
    @NonNull
    String toString();

    boolean equals(Object obj);

    int getViewType();

    Object getContent();

    @Nullable
    IExpandableRecyclerItem getParent();
}
