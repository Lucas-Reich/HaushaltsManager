package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.SelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewSelectedItemHandler extends RecyclerViewExpandableItemHandler {
    private final SelectionRules selectionRules;
    private final List<IRecyclerItem> selectedItems;

    public RecyclerViewSelectedItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy, SelectionRules selectionRules) {
        super(items, insertStrategy);

        this.selectionRules = selectionRules;
        selectedItems = new ArrayList<>();
    }

    @Override
    public void remove(IRecyclerItem item) {
        selectedItems.remove(item);

        super.remove(item);
    }

    public void select(IRecyclerItem item) {
        if (!selectionRules.canBeSelected(item, selectedItems)) {
            return;
        }

        selectedItems.add(item);
        notifyItemChanged(indexOf(item));
    }

    public void unselect(IRecyclerItem item) {
        if (!selectedItems.contains(item)) {
            return;
        }

        selectedItems.remove(item);
        notifyItemChanged(indexOf(item));
    }

    public void clearSelection() {
        if (selectedItems.isEmpty()) {
            return;
        }

        for (IRecyclerItem item : selectedItems) {
            unselect(item);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public int getSelectedParentCount() {
        int itemCount = 0;

        for (IRecyclerItem item : selectedItems) {
            if (!(item instanceof ChildExpenseItem)) {
                itemCount++;
            }
        }

        return itemCount;
    }

    public int getSelectedChildCount() {
        int childCount = 0;

        for (IRecyclerItem item : selectedItems) {
            if (item instanceof ChildExpenseItem) {
                childCount++;
            }
        }

        return childCount;
    }

    public boolean isInSelectionMode() {
        return selectedItems.size() > 0;
    }

    public List<IRecyclerItem> getSelectedItems() {
        return selectedItems;
    }

    public boolean isSelected(IRecyclerItem item) {
        return selectedItems.contains(item);
    }
}
