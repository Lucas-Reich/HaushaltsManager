package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.TemplateListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.TemplateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.TemplateViewHolder;

import java.util.List;

public class TemplateListRecyclerViewAdapter extends RecyclerViewItemHandler {
    public TemplateListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new TemplateListInsertStrategy());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TemplateItem.VIEW_TYPE) {
            return new TemplateViewHolder(inflater.inflate(
                    R.layout.recycler_view_template,
                    parent,
                    false)
            );
        }

        return new GenericViewHolder(inflater.inflate(
                R.layout.recycler_view_generic,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
}
