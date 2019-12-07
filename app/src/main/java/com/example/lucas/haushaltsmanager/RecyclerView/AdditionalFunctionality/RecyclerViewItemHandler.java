package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.InsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewItemHandler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ListHandler listHandler;

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        return new GenericViewHolder(inflater.inflate(
                R.layout.recycler_view_generic,
                viewGroup,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        viewHolder.bind(item);
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
        IRecyclerItem parent = item.getParent();

        if (null == parent) {

            listHandler.insertParent(item);
            return;
        }

        if (listHandler.parentExists(parent)) {

            listHandler.addItemToParent(item);
            return;
        }

        insertItem(parent);
        listHandler.addItemToParent(item);
    }

    /**
     * Diese Funktion wird genutzt, wenn ein Item aus der Liste gelöscht werden soll.
     */
    public void removeItem(IRecyclerItem item) {
        IRecyclerItem parent = item.getParent();

        listHandler.remove(item);

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
        listHandler.remove(item);
    }

    void updateItem(IRecyclerItem item) {
        notifyItemChanged(listHandler.indexOf(item));
    }

    private boolean hasChildren(IRecyclerItem parent) {
        int children = listHandler.getChildrenCount(parent);

        return 0 < children;
    }

    private class ListHandler {
        private List<IRecyclerItem> mItems;
        private InsertStrategy mInsertStrategy;

        public ListHandler(InsertStrategy insertStrategy) {
            mItems = new ArrayList<>();
            mInsertStrategy = insertStrategy;
        }

        public boolean parentExists(IRecyclerItem parent) {
            return mItems.contains(parent);
        }

        public List<IRecyclerItem> getItems() {
            return mItems;
        }

        public int getChildrenCount(IRecyclerItem parent) {
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

        public void remove(IRecyclerItem item) {
            int itemIndex = mItems.indexOf(item);

            mItems.remove(item);

            notifyItemRemoved(itemIndex);
        }

        public void insertParent(IRecyclerItem parent) {
            int insertedIndex = mInsertStrategy.insert(parent, mItems);

            notifyItemInserted(insertedIndex);
        }

        public int indexOf(IRecyclerItem item) {
            return mItems.indexOf(item);
        }

        private void addItemToParent(IRecyclerItem item) {
            int insertedIndex = mInsertStrategy.insert(item, mItems);

            if (insertedIndex != InsertStrategy.INVALID_INDEX) {
                notifyItemInserted(insertedIndex);
            }
        }
    }
}
