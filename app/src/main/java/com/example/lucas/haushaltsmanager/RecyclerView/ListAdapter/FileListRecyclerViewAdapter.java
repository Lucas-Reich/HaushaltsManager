package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.FileListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

public class FileListRecyclerViewAdapter extends RecyclerViewItemHandler {
    public FileListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new FileListInsertStrategy());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == FileItem.VIEW_TYPE) {
            return new FileViewHolder(inflater.inflate(
                    R.layout.recycler_view_file,
                    parent,
                    false
            ));
        }

        return super.onCreateViewHolder(parent, viewType);
    }
}
