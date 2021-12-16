package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy

import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem

class FileItemInsertStrategy : InsertStrategy {
    companion object {
        private const val DESC = "desc"
    }

    override fun insert(item: IRecyclerItem, items: MutableList<IRecyclerItem>): Int {
        if (item !is FileItem) {
            throw IllegalArgumentException(
                String.format(
                    "FileListInsertStrategy requires FileItems. Class given: %s",
                    item.javaClass.simpleName
                )
            )
        }

        val insertIndex = getInsertIndex(item, items)
        items.add(insertIndex, item)

        return insertIndex
    }

    private fun getInsertIndex(fileItem: FileItem, fileItems: List<IRecyclerItem>): Int {
        for (existingFileItem in fileItems) {
            if (canInsert(fileItem, existingFileItem as FileItem, DESC)) {
                return fileItems.indexOf(existingFileItem)
            }
        }

        return fileItems.size
    }

    private fun canInsert(fileItem: FileItem, existingFileItem: FileItem, sortingStrategy: String): Boolean {
        val itemLastModified = fileItem.content.lastModified()
        val otherItemLastModified = existingFileItem.content.lastModified()

        if (DESC == sortingStrategy) {
            return itemLastModified > otherItemLastModified
        }

        return itemLastModified < otherItemLastModified
    }
}