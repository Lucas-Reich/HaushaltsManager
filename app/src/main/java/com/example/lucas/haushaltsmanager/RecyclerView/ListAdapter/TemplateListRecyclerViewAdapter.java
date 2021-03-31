package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.TemplateListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateViewHolder;

import java.util.List;

public class TemplateListRecyclerViewAdapter extends RecyclerViewItemHandler {
    public TemplateListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new TemplateListInsertStrategy());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TemplateItem.VIEW_TYPE) {
            return new TemplateViewHolder(inflater.inflate(
                    R.layout.recycler_view_template,
                    parent,
                    false)
            );
        }

        return super.onCreateViewHolder(parent, viewType);
    }
}
