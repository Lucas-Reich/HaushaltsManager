package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.List;

public class ExpenseListInsertStrategy implements InsertStrategy {

    @Override
    public int insert(IRecyclerItem item, List<IRecyclerItem> items) {
        IParentRecyclerItem parentItem = item.getParent();

        if (null == parentItem) {
            return insertParent(item, items);
        }

        parentItem.addChild(item);

        if (parentItem.isExpanded()) {
            return insertItemAfterParent(item, items);
        }

        return INVALID_INDEX;
    }

    private boolean canInsert(DateItem dateItem, IRecyclerItem parent) {
        return dateItem.getContent().before(parent.getContent());
    }

    private void assertCorrectInstance(IRecyclerItem parent) throws IllegalArgumentException {
        if (parent instanceof DateItem) {
            return;
        }

        String wrongClass = parent == null ? "null" : parent.getClass().getSimpleName();

        throw new IllegalArgumentException(String.format(
                "ExpenseListInsertStrategy requires DateItem as parents. Class given: %s",
                wrongClass
        ));
    }

    private int insertItemAfterParent(IRecyclerItem child, List<IRecyclerItem> items) {
        int afterParentIndex = getAfterParentIndex(child.getParent(), items);

        if (INVALID_INDEX != afterParentIndex) {
            items.add(afterParentIndex, child);
        }

        return afterParentIndex;
    }

    private int getAfterParentIndex(IRecyclerItem parent, List<IRecyclerItem> items) {
        int parentIndex = items.indexOf(parent);

        if (parentIndex >= 0) {
            return parentIndex + 1;
        }

        return INVALID_INDEX;
    }

    private int insertParent(IRecyclerItem parent, List<IRecyclerItem> items) {
        assertCorrectInstance(parent);

        int parentInsertIndex = getDateInsertIndex((DateItem) parent, items);

        items.add(parentInsertIndex, parent);

        return parentInsertIndex;
    }

    private int getDateInsertIndex(DateItem dateItem, List<IRecyclerItem> items) {
        for (IRecyclerItem item : items) {
            if (!(item instanceof DateItem)) {
                continue;
            }

            DateItem currentDate = (DateItem) item;

            if (canInsert(currentDate, dateItem)) {

                return items.indexOf(currentDate);
            }
        }

        return items.size();
    }
}
