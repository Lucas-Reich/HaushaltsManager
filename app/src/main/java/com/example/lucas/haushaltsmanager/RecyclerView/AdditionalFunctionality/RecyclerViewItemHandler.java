package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewItemHandler extends RecyclerView.Adapter<AbstractViewHolder> {
    private final List<IRecyclerItem> items;
    private final InsertStrategy insertStrategy;

    public RecyclerViewItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy) {
        this.items = new ArrayList<>();
        this.insertStrategy = insertStrategy;

        insertAll(items);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        return new GenericViewHolder(inflater.inflate(
                R.layout.recycler_view_generic,
                viewGroup,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        IRecyclerItem item = get(position);
        holder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        IRecyclerItem item = get(position);
        if (null == item) {
            throw new IndexOutOfBoundsException(String.format(
                    "Cannot resolve view of item at position '%s'. Max position is '%s'",
                    position,
                    getItemCount()
            ));
        }

        return item.getViewType();
    }

    public void insert(IRecyclerItem item) {
        int insertedItemIndex = insertStrategy.insert(item, items);
        notifyItemInserted(insertedItemIndex);
    }

    public void insertAll(List<IRecyclerItem> items) {
        for (IRecyclerItem item : items) {
            insert(item);
        }
    }

    @Nullable
    public IRecyclerItem get(int index) {
        if (index > getItemCount()) {
            return null;
        }

        return items.get(index);
    }

    public boolean exists(IRecyclerItem item) {
        return items.contains(item);
    }

    void update(IRecyclerItem item) {
        notifyItemChanged(indexOf(item));
    }

    void remove(IRecyclerItem item) {
        int removedItemIndex = indexOf(item);

        items.remove(removedItemIndex);

        notifyItemRemoved(removedItemIndex);
    }

    public int indexOf(IRecyclerItem item) {
        return items.indexOf(item);
    }
}
