package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.List;

/**
 * Basierend auf dem Erstellungszeitpunkt der Datei wird diese in die Liste eingefügt.
 */
public class FileListInsertStrategy implements InsertStrategy {
    private static final String DSC = "dsc";

    @Override
    public int insert(IRecyclerItem newFileItem, List<IRecyclerItem> fileItems) {
        assertCorrectInstance(newFileItem);

        int insertIndex = getInsertIndex(newFileItem, fileItems);
        fileItems.add(insertIndex, newFileItem);

        return insertIndex;
    }

    private int getInsertIndex(IRecyclerItem fileItem, List<IRecyclerItem> fileItems) {
        for (IRecyclerItem existingFileItem : fileItems) {
            if (!canInsert((FileItem) fileItem, (FileItem) existingFileItem, DSC)) {
                continue;
            }

            return fileItems.indexOf(existingFileItem);
        }

        return fileItems.size();
    }

    private boolean canInsert(FileItem fileItem, FileItem existingFileItem, String sortingStrategy) {
        long itemLastModified = fileItem.getContent().lastModified();
        long otherItemLastModified = existingFileItem.getContent().lastModified();

        if (sortingStrategy.equals(DSC)) {
            return itemLastModified > otherItemLastModified;
        }

        return itemLastModified < otherItemLastModified;
    }

    private void assertCorrectInstance(IRecyclerItem item) {
        if (item instanceof FileItem) {
            return;
        }

        throw new IllegalArgumentException(String.format("FileListInsertStrategy requires FileItems. Class given: %s", item.getClass().getSimpleName()));
    }
}
