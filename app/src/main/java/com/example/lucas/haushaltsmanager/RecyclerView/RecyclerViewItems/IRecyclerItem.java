package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import android.support.annotation.NonNull;

public interface IRecyclerItem {
    @NonNull
    String toString();

    boolean equals(Object obj);

    int getViewType();

    Object getContent();

    IParentRecyclerItem getParent();
}
