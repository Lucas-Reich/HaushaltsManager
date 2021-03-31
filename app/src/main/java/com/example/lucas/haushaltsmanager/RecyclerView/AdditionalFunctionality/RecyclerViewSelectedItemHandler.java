package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.SelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildExpenseItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

// REFACTORING: Ich kann die Selection Implementierung hiermit https://proandroiddev.com/a-guide-to-recyclerview-selection-3ed9f2381504 austauschen
public abstract class RecyclerViewSelectedItemHandler extends RecyclerViewExpandableItemHandler {
    private SelectionRules selectionRules;
    private List<IRecyclerItem> selectedItems;

    public RecyclerViewSelectedItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy, SelectionRules selectionRules) {
        super(items, insertStrategy);

        this.selectionRules = selectionRules;
        selectedItems = new ArrayList<>();
    }

    @Override
    public void removeItem(IRecyclerItem item) {
        if (isItemSelected(item)) {
            selectedItems.remove(item);
        }

        super.removeItem(item);
    }

    public void selectItem(IRecyclerItem item, int position) {
        if (!selectionRules.canBeSelected(item, selectedItems)) {
            return;
        }

        selectedItems.add(item);
        notifyItemChanged(position);
    }

    public void unselectItem(IRecyclerItem item, int position) {
        if (!selectedItems.contains(item)) {
            return;
        }

        selectedItems.remove(item);
        notifyItemChanged(position);
    }

    public void clearSelections() {
        if (selectedItems.isEmpty()) {
            return;
        }

        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public int getSelectedItemsCount() {
        int itemCount = 0;

        for (IRecyclerItem item : selectedItems) {
            if (!(item instanceof ChildExpenseItem)) {
                itemCount++;
            }
        }

        return itemCount;
    }

    public boolean isInSelectionMode() {
        return selectedItems.size() > 0;
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

    public List<IRecyclerItem> getSelectedItems() {
        return selectedItems;
    }

    public boolean isItemSelected(IRecyclerItem item) {
        return selectedItems.contains(item);
    }
}
