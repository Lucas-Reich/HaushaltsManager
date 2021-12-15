package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ISelectableRecyclerItem;

import java.util.List;

public class ExpenseListSelectionRules implements SelectionRules {
    @Override
    public boolean canBeSelected(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        if (!(item instanceof ISelectableRecyclerItem)) {
            return false;
        }

        if (item instanceof ExpenseItem && noChildrenAreSelected(otherSelectedItems)) { // ExpenseItem cannot be selected if ChildItem is selected as well
            return true;
        }

        if (hasDifferentParent(item, otherSelectedItems)) {
            return false;
        }

        if (isAlreadySelected(item, otherSelectedItems)) {
            return false;
        }

        return true;
    }

    private boolean noChildrenAreSelected(List<IRecyclerItem> selectedItems) {
        for (IRecyclerItem selectedItem : selectedItems) {
            if (selectedItem instanceof ChildExpenseItem) {
                return false;
            }
        }

        return true;
    }

    private boolean isAlreadySelected(IRecyclerItem item, List<IRecyclerItem> selectedItems) {
        return selectedItems.contains(item);
    }

    private boolean hasDifferentParent(IRecyclerItem item, List<IRecyclerItem> otherSelectedItems) {
        for (IRecyclerItem selectedItem : otherSelectedItems) {
            if (!itemsHaveSameParent(item, selectedItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean itemsHaveSameParent(IRecyclerItem item, IRecyclerItem other) {
        return item.getParent().equals(other.getParent());
    }
}
