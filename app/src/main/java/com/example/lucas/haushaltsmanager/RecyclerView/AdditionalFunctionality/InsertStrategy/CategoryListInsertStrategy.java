package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryItem;

import java.util.List;

/**
 * Die CategoryListInsertStrategy fügt den neuen Parent einfach an das Ende der Liste an,
 * da es für die Kategorien keine besondere Ordnung gibt.
 */
public class CategoryListInsertStrategy implements InsertStrategy {

    @Override
    public int insert(IRecyclerItem item, List<IRecyclerItem> items) {
        IParentRecyclerItem parentItem = item.getParent();

        if (null == parentItem) {
            return insertParent(item, items);
        }

        parentItem.addChild(item);

        if (parentItem.isExpanded()) {
            return insertAfterParent(item, items);
        }

        return INVALID_INDEX;
    }

    private int insertParent(IRecyclerItem parent, List<IRecyclerItem> items) {
        assertCorrectInstance(parent);

        return appendItem(parent, items);
    }

    private int appendItem(IRecyclerItem item, List<IRecyclerItem> items) {
        items.add(item);

        return items.size() - 1;
    }

    private int insertAfterParent(IRecyclerItem child, List<IRecyclerItem> items) {
        int indexAfterParent = getAfterParentIndex(child.getParent(), items);

        if (indexAfterParent != INVALID_INDEX) {
            items.add(indexAfterParent, child);
        }

        return indexAfterParent;
    }

    private int getAfterParentIndex(IRecyclerItem parent, List<IRecyclerItem> items) {
        int parentIndex = items.indexOf(parent);

        if (parentIndex >= 0) {
            return parentIndex + 1;
        }

        return INVALID_INDEX;
    }

    private void assertCorrectInstance(IRecyclerItem item) throws IllegalArgumentException {
        if (item instanceof ParentCategoryItem) {
            return;
        }

        String wrongClass = item == null ? "null" : item.getClass().getSimpleName();
        throw new IllegalArgumentException(String.format(
                "CategoryListInsertStrategy requires ParentCategoryItems as parents. Class given: %s",
                wrongClass
        ));
    }
}
