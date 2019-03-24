package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

public class AdViewHolder extends AbstractViewHolder {
    private static final String TAG = AdViewHolder.class.getSimpleName();

    public AdViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof AdItem)) {
            throw new IllegalArgumentException(String.format("Wrong type given in %s", TAG));
        }
    }
}
