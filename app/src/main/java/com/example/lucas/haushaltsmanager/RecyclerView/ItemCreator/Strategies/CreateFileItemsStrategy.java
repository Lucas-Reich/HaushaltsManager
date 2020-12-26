package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateFileItemsStrategy implements RecyclerItemCreatorStrategyInterface<File> {
    @Override
    public List<IRecyclerItem> create(List<File> files) {
        if (files.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> fileItems = new ArrayList<>();
        for (File file : files) {
            fileItems.add(new FileItem(file));
        }

        return fileItems;
    }
}
