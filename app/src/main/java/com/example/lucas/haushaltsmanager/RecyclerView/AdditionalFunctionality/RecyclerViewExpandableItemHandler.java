package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public abstract class RecyclerViewExpandableItemHandler extends RecyclerViewItemHandler {

    public RecyclerViewExpandableItemHandler(List<IRecyclerItem> items) {
        super(items);
    }

    public void toggleExpansion(int position) {
        IRecyclerItem item = getItem(position);

        if (item.canExpand()) {
            List<IRecyclerItem> childItems = item.getChildren();

            if (item.isExpanded()) {
                removeItemRange(position, childItems.size());
                item.setExpanded(false);
            } else {
                insertItemRange(position, childItems);
                item.setExpanded(true);
            }

            notifyItemChanged(position);
        }
    }

    public void insertItem(IRecyclerItem item) {
        if (item instanceof ChildItem) {
            ChildItem childItem = (ChildItem) item;

            PositionHolder parentPosition = getParentPosition(childItem);
            if (!parentPosition.isInvalid) {
                mItems.get(parentPosition.datePos).get(parentPosition.relPos).getChildren().add(childItem);

                if (isParentExpanded(parentPosition)) {
                    mItems.get(parentPosition.datePos).add(parentPosition.relPos + 1, childItem);
                    increaseItemCount(parentPosition.datePos);
                    notifyItemInserted(parentPosition.absPos + 1);
                }
            } else {
                super.insertItem(new ExpenseItem(childItem.getContent()));
            }
        } else {

            super.insertItem(item);
        }
    }

    public void removeItem(int position) {
        if (isChildItem(position)) {
            removeChildItem(position);
        } else {
            super.removeItem(position);
        }
    }

    private void removeChildItem(int position) {
        ChildItem child = (ChildItem) getItem(position);

        PositionHolder parentPosition = getParentPosition(child);

        removeChildItemFromParent(parentPosition, child);
        super.removeItem(position);

        if (!hasChildren(parentPosition)) {
            super.removeItem(parentPosition.absPos);
        }
    }

    private void removeChildItemFromParent(PositionHolder parentPosition, ChildItem child) {
        mItems.get(parentPosition.datePos)
                .get(parentPosition.relPos)
                .getChildren()
                .remove(child);
    }

    private void insertItemRange(int afterPosition, List<IRecyclerItem> items) {
        PositionHolder index = toInternalPosition(afterPosition);

        mItems.get(index.datePos).addAll(index.relPos + 1, items);

        for (int i = 0; i < items.size(); i++) {
            increaseItemCount(index.datePos);
        }

        notifyItemRangeInserted(index.absPos + 1, items.size());
    }

    private void removeItemRange(int afterPosition, int amount) {
        PositionHolder index = toInternalPosition(afterPosition);

        mItems.get(index.datePos).subList(index.relPos + 1, index.relPos + 1 + amount).clear();

        for (int i = 0; i < amount; i++) {
            decreaseItemCount(index.datePos);
        }

        notifyItemRangeRemoved(index.absPos + 1, amount);
    }

    private boolean isChildItem(int position) {
        return getItem(position) instanceof ChildItem;
    }

    private PositionHolder getParentPosition(ChildItem child) {
        int absPos = 1;

        for (Map.Entry<Calendar, List<IRecyclerItem>> entry : mItems.entrySet()) {
            for (IRecyclerItem item : entry.getValue()) {
                if (item instanceof ParentExpenseItem && ((ParentExpenseItem) item).hasChild(child.getContent())) {
                    int itemIndex = entry.getValue().indexOf(item);
                    entry.getValue().set(itemIndex, item);
                    return new PositionHolder(entry.getKey(), itemIndex, absPos);
                }

                absPos++;
            }
        }

        return PositionHolder.createInvalidPosition();
    }

    private boolean isParentExpanded(PositionHolder parentPosition) {
        return mItems.get(parentPosition.datePos).get(parentPosition.relPos).isExpanded();
    }

    private boolean hasChildren(PositionHolder position) {
        IRecyclerItem item = getItem(position.absPos);

        if (item instanceof ParentExpenseItem) {
            return item.getChildren().size() != 0;
        }

        return true;
    }
}
