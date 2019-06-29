package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 9;
    private static final String TAG = FileItem.class.getSimpleName();

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
    public boolean canExpand() {
        return false;
    }

    @Override
    public boolean isExpanded() {
        return false;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        throw new IllegalStateException(String.format("setExpanded method called on a Object that cannot expand: %s", TAG));
    }

    @Override
    public IRecyclerItem getParent() {
        return null;
    }

    @Override
    public List<IRecyclerItem> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public void addChild(IRecyclerItem item) {
        // Do nothing
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
