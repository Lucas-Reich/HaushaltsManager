package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public abstract class RecyclerViewItemHandler extends RecyclerViewSelectedItemHandler {
    TreeMap<Calendar, List<IRecyclerItem>> mItems;
    private TreeMap<Calendar, Integer> mItemCount;

    RecyclerViewItemHandler(List<IRecyclerItem> items) {
        mItems = new TreeMap<>(Collections.<Calendar>reverseOrder());
        mItemCount = new TreeMap<>(Collections.<Calendar>reverseOrder());

        insertAll(items);
    }

    @Override
    public int getItemCount() {
        int counter = 0;

        for (Integer itemCount : mItemCount.values()) {
            counter += itemCount;

            if (0 != itemCount) {
                counter++;
            }
        }

        return counter;
    }

    public void insertItem(final IRecyclerItem item) {
        PositionHolder newItemIndex = addToItems(item);

        if (mItemCount.get(newItemIndex.datePos) == 1) {
            notifyItemInserted(newItemIndex.absPos - 1);
        }

        notifyItemInserted(newItemIndex.absPos);
    }

    private PositionHolder addToItems(final IRecyclerItem item) {
        Calendar secureDate = secureDate(item.getDate());

        if (mItems.containsKey(secureDate)) {
            mItems.get(secureDate).add(item);
        } else {
            mItemCount.put(secureDate, 0);
            mItems.put(secureDate, new ArrayList<IRecyclerItem>() {{
                add(item);
            }});
        }

        increaseItemCount(secureDate);

        return toInternalPosition(item);
    }

    public void insertAll(List<IRecyclerItem> items) {
        for (IRecyclerItem item : items) {
            insertItem(item);
        }
    }

    public void removeItem(int position) {
        PositionHolder index = toInternalPosition(position);

        if (!index.isInvalid) {
            mItems.get(index.datePos).remove(index.relPos);

            decreaseItemCount(index.datePos);

            notifyItemRemoved(index.absPos);

            if (mItemCount.get(index.datePos) == 0) {
                notifyItemRemoved(index.absPos - 1);
            }
        }
    }

    public IRecyclerItem getItem(int position) {
        PositionHolder index = toInternalPosition(position);

        if (!index.isInvalid) {
            if (index.relPos == -1) {
                return new DateItem(index.datePos);
            } else {
                return mItems.get(index.datePos).get(index.relPos);
            }
        }

        throw new IndexOutOfBoundsException(String.format("Could not find Item at position %d", position));
    }

    void increaseItemCount(Calendar date) {
        mItemCount.put(date, mItemCount.get(date) + 1);
    }

    void decreaseItemCount(Calendar date) {
        mItemCount.put(date, mItemCount.get(date) - 1);
    }

    PositionHolder toInternalPosition(int position) {
        int relPos = position;

        for (Map.Entry<Calendar, Integer> entry : mItemCount.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }

            if (entry.getValue() < relPos) {
                relPos -= entry.getValue() + 1;
                continue;
            }

            return new PositionHolder(entry.getKey(), relPos - 1, position);
        }

        return PositionHolder.createInvalidPosition();
    }

    private PositionHolder toInternalPosition(IRecyclerItem item) {
        Calendar secureDate = secureDate(item.getDate());

        int relPos = mItems.get(secureDate).indexOf(item);
        int absPos = relPos;
        for (Map.Entry<Calendar, Integer> entry : mItemCount.entrySet()) {
            if (entry.getKey().equals(secureDate)) {
                absPos++;
                break;
            }

            absPos += entry.getValue() + 1;
        }

        return new PositionHolder(secureDate, relPos, absPos);
    }

    private Calendar secureDate(Calendar date) {
        Calendar secureDate = Calendar.getInstance();
        secureDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
        secureDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
        secureDate.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
        secureDate.set(Calendar.HOUR_OF_DAY, 0);
        secureDate.set(Calendar.MINUTE, 0);
        secureDate.set(Calendar.SECOND, 0);
        secureDate.set(Calendar.MILLISECOND, 0);

        return secureDate;
    }
}
