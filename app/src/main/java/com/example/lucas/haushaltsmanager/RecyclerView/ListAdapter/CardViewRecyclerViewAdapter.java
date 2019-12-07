package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem.CardViewItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CardViewItem.CardViewHolder;

import java.util.List;

public class CardViewRecyclerViewAdapter extends RecyclerViewItemHandler {
    public CardViewRecyclerViewAdapter(List<IRecyclerItem> items, InsertStrategy insertStrategy) {
        super(items, insertStrategy);
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == CardViewItem.VIEW_TYPE) {
            return new CardViewHolder(inflater.inflate(
                    R.layout.recycler_view_card_item,
                    parent,
                    false
            ));
        }

        return super.onCreateViewHolder(parent, viewType);
    }
}
