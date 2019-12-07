package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.CategoryListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewSelectedItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.CategoryListSelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildCategoryItem.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildCategoryItem.ChildCategoryViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryViewHolder;

import java.util.List;

public class CategoryListRecyclerViewAdapter extends RecyclerViewSelectedItemHandler {
    public CategoryListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new CategoryListInsertStrategy(), new CategoryListSelectionRules());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ChildCategoryItem.VIEW_TYPE:

                View childCategoryView = inflater.inflate(R.layout.recycler_view_child_category, parent, false);
                return new ChildCategoryViewHolder(childCategoryView);
            case ParentCategoryItem.VIEW_TYPE:

                View parentCategoryView = inflater.inflate(R.layout.recycler_view_parent_category, parent, false);
                return new ParentCategoryViewHolder(parentCategoryView);
            default:

                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        holder.itemView.setSelected(isItemSelected(item));
        holder.bind(item);
    }
}
