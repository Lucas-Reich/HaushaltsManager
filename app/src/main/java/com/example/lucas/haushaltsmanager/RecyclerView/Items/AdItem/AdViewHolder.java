package com.example.lucas.haushaltsmanager.RecyclerView.Items.AdItem;

import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;

public class AdViewHolder extends AbstractViewHolder {
    private static final String TAG = AdViewHolder.class.getSimpleName();

    public AdViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof AdItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }
    }
}
