package com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.io.File;

public class FileViewHolder extends AbstractViewHolder {
    private static final String TAG = FileViewHolder.class.getSimpleName();

    private TextView title;

    public FileViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.recycler_view_file_title);
    }

    @Override
    public void bind(IRecyclerItem item) {
        if (!(item instanceof FileItem)) {
            throw new IllegalArgumentException(String.format("Could not attach %s to %s", item.getClass().getSimpleName(), TAG));
        }

        File file = (File) item.getContent();

        setTitle(file.getName());
    }

    private void setTitle(String title) {
        this.title.setText(title);
    }
}
