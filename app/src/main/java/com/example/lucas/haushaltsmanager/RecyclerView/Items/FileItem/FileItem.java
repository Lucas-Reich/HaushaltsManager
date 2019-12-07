package com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.io.File;

public class FileItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 9;

    private File file;

    public FileItem(File file) {
        this.file = file;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public File getContent() {
        return file;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return file.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof FileItem)) {
            return false;
        }

        FileItem other = (FileItem) obj;

        return other.getContent().equals(getContent());
    }
}
