package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RecyclerViewExpandableItemHandler extends RecyclerViewItemHandler {
    public RecyclerViewExpandableItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy) {
        super(items, insertStrategy);
    }

    @Override
    public void removeItem(IRecyclerItem item) {
        if (item instanceof ChildExpenseItem) {

            ParentExpenseItem parent = (ParentExpenseItem) item.getParent();
            parent.removeChild(item);

            updateItem(parent);
        }

        super.removeItem(item);
    }

    public void toggleExpansion(int position) {
        IRecyclerItem parent = getItem(position);

        if (parent.canExpand()) {
            handleExpansion(parent);
        }
    }

    private void handleExpansion(IRecyclerItem expandableItem) {
        // TODO: Expansion strategie, ohne auch immer den Parent updaten zu müssen:
        //  Expand: Children des Parents werden eglsöcht und in die Liste eingefügt.
        //  Collapse: Children werden aus der Liste gelöscht und dem Parent hizugefügt.
        List<IRecyclerItem> children = expandableItem.getChildren();

        if (expandableItem.isExpanded()) {
            removeChildren(children);
            expandableItem.setExpanded(!expandableItem.isExpanded());
        } else {
            expandableItem.setExpanded(!expandableItem.isExpanded());
            insertChildren(children);
        }

        updateItem(expandableItem);
    }

    private void insertChildren(List<IRecyclerItem> items) {
        List<IRecyclerItem> copy = new ArrayList<>(items);
        Collections.reverse(copy);

        insertAll(copy);
    }

    private void removeChildren(List<IRecyclerItem> items) {
        for (IRecyclerItem item : items) {
            deleteItem(item);
        }
    }
}
