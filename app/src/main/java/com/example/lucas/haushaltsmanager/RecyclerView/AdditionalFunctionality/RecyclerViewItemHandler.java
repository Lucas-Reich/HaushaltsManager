package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IExpandableRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewItemHandler extends RecyclerView.Adapter<AbstractViewHolder> {
    private final ListHandler listHandler;

    public RecyclerViewItemHandler(List<IRecyclerItem> items, InsertStrategy insertStrategy) {
        listHandler = new ListHandler(insertStrategy);

        insertAll(items);
    }

    @Override
    public int getItemCount() {
        return listHandler.count();
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        return new GenericViewHolder(inflater.inflate(
                R.layout.recycler_view_generic,
                viewGroup,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);
        holder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    public void insertAll(List<IRecyclerItem> items) {
        for (IRecyclerItem item : items) {
            insertItem(item);
        }
    }

    public void insertItem(IRecyclerItem item) {
        IExpandableRecyclerItem parent = item.getParent();

        if (null == parent || listHandler.parentExists(parent)) {

            int insertedItemIndex = listHandler.insert(item);
            notifyItemInserted(insertedItemIndex);
            return;
        }

        insertItem(parent);
        listHandler.insert(item);
    }

    /**
     * Diese Funktion wird genutzt, wenn ein Item aus der Liste gelöscht werden soll.
     */
    public void removeItem(IRecyclerItem item) {
        IExpandableRecyclerItem parent = item.getParent();

        int removedItemIndex = listHandler.remove(item);
        notifyItemRemoved(removedItemIndex);

        if (parent != null && !hasChildren(parent)) {
            removeItem(parent);
        }
    }

    public IRecyclerItem getItem(int position) {
        return listHandler.getItems().get(position);
    }

    /**
     * Diese Funktion wird genutzt, wenn ein Parent zusammengeklappt wird.
     */
    void deleteItem(IRecyclerItem item) {
        int removedItemIndex = listHandler.remove(item);
        notifyItemRemoved(removedItemIndex);
    }

    void updateItem(IRecyclerItem item) {
        notifyItemChanged(listHandler.indexOf(item));
    }

    private boolean hasChildren(IExpandableRecyclerItem parent) {
        int children = listHandler.getChildrenCount(parent);

        return 0 < children;
    }

    private class ListHandler {
        private final List<IRecyclerItem> mItems;
        private final InsertStrategy mInsertStrategy;

        public ListHandler(InsertStrategy insertStrategy) {
            mItems = new ArrayList<>();
            mInsertStrategy = insertStrategy;
        }

        public boolean parentExists(IExpandableRecyclerItem parent) {
            return mItems.contains(parent);
        }

        public List<IRecyclerItem> getItems() {
            return mItems;
        }

        public int getChildrenCount(IExpandableRecyclerItem parent) {
            int index = mItems.indexOf(parent) + 1;
            int childrenCount = 0;

            while (index < mItems.size() && parent.equals(mItems.get(index).getParent())) {
                childrenCount++;
                index++;
            }

            return childrenCount;
        }

        public int count() {
            return mItems.size();
        }

        public Integer remove(IRecyclerItem item) {
            int itemIndex = mItems.indexOf(item);

            mItems.remove(item);

            return itemIndex;
        }

        public Integer insert(IRecyclerItem parent) {
            return mInsertStrategy.insert(parent, mItems);
        }

        public int indexOf(IRecyclerItem item) {
            return mItems.indexOf(item);
        }
    }
}
