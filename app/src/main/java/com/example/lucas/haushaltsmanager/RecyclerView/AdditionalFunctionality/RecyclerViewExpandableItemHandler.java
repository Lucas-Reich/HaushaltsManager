package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.util.Log;

import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RecyclerViewExpandableItemHandler extends RecyclerViewItemHandler {
    private static final String TAG = RecyclerViewExpandableItemHandler.class.getSimpleName();

    RecyclerViewExpandableItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy) {
        super(items, insertStrategy);
    }

    @Override
    public void remove(IRecyclerItem item) {
        super.remove(item);

        if (item instanceof ChildExpenseItem) {
            IExpandableRecyclerItem parent = item.getParent();
            parent.removeChild(item);

            if (0 == parent.getChildren().size()) {
                super.remove(parent);
            }
        }
    }

    @Override
    public void insert(IRecyclerItem item) {
        IExpandableRecyclerItem parent = item.getParent();
        if (null == item.getParent() && exists(parent)) {
            super.insert(item);
            return;
        }

        super.insert(parent);
        super.insert(item);
    }

    public void toggleExpansion(int position) {
        IRecyclerItem item = get(position);

        if (!(item instanceof IExpandableRecyclerItem)) {

            Log.i(TAG, String.format("Tried to toggle expansion non expandable item: %s", item.getClass().getSimpleName()));
            return;
        }

        handleExpansion((IExpandableRecyclerItem) item);
    }

    private void handleExpansion(IExpandableRecyclerItem expandableItem) {
        for (IRecyclerItem item : expandableItem.getChildren()) {
            if (expandableItem.isExpanded()) {
                remove(item);
                continue;
            }

            insert(item);
        }

        expandableItem.setExpanded(!expandableItem.isExpanded());

        update(expandableItem);
    }
}
