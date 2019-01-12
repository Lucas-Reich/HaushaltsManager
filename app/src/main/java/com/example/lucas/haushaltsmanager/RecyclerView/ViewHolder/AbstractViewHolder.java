package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(IRecyclerItem item);
}
