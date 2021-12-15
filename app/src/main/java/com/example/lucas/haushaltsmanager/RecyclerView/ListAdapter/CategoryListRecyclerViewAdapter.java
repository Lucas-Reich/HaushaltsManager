package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewSelectedItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.SelectIfNotSelectedSelectionRule;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem.CategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem.CategoryViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public class CategoryListRecyclerViewAdapter extends RecyclerViewSelectedItemHandler {
    public CategoryListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new AppendInsertStrategy(), new SelectIfNotSelectedSelectionRule());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == CategoryItem.VIEW_TYPE) {

            View parentCategoryView = inflater.inflate(R.layout.recycler_view_parent_category, parent, false);
            return new CategoryViewHolder(parentCategoryView);
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        IRecyclerItem item = get(position);

        holder.itemView.setActivated(isItemSelected(item));
        holder.bind(item);
    }
}
