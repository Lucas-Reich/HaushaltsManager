package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.FileListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.FileViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;

import java.util.List;

public class FileListRecyclerViewAdapter extends RecyclerViewItemHandler {
    public FileListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new FileListInsertStrategy());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == FileItem.VIEW_TYPE) {
            return new FileViewHolder(inflater.inflate(
                    R.layout.recycler_view_file,
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
