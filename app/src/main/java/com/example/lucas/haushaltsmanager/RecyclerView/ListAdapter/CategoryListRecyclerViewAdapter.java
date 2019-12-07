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
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ChildCategoryViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ParentCategoryViewHolder;

import java.util.List;

public class CategoryListRecyclerViewAdapter extends RecyclerViewSelectedItemHandler {
    public CategoryListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new CategoryListInsertStrategy(), new CategoryListSelectionRules());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ChildCategoryItem.VIEW_TYPE:

                View childCategoryView = inflater.inflate(R.layout.recycler_view_child_category, parent, false);
                return new ChildCategoryViewHolder(childCategoryView);
            case ParentCategoryItem.VIEW_TYPE:

                View parentCategoryView = inflater.inflate(R.layout.recycler_view_parent_category, parent, false);
                return new ParentCategoryViewHolder(parentCategoryView);
            default:

                View genericView = inflater.inflate(R.layout.recycler_view_generic, parent, false);
                return new GenericViewHolder(genericView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        viewHolder.itemView.setSelected(isItemSelected(item));
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
}
