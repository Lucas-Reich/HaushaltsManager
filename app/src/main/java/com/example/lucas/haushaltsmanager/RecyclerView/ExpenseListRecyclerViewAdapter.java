package com.example.lucas.haushaltsmanager.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewExpandableItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AdViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ChildViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.DateViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ExpenseItemViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ParentExpenseViewHolder;

import java.util.List;

public class ExpenseListRecyclerViewAdapter extends RecyclerViewExpandableItemHandler {
    public ExpenseListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items);
    }

    // TODO Der GenericViewHolder sollte hier in dem Adapter definiert werden

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case DateItem.VIEW_TYPE:

                View dateView = inflater.inflate(R.layout.recycler_view_date, parent, false);
                return new DateViewHolder(dateView);
            case ExpenseItem.VIEW_TYPE:

                View expenseView = inflater.inflate(R.layout.recycler_view_expense, parent, false);
                return new ExpenseItemViewHolder(expenseView);
            case ParentExpenseItem.VIEW_TYPE:

                View parentExpenseView = inflater.inflate(R.layout.recycler_view_parent, parent, false);
                return new ParentExpenseViewHolder(parentExpenseView);
            case AdItem.VIEW_TYPE:

                View AdView = inflater.inflate(R.layout.recycler_view_expense, parent, false);
                return new AdViewHolder(AdView);
            case ChildItem.VIEW_TYPE:

                View childView = inflater.inflate(R.layout.recycler_view_child, parent, false);
                return new ChildViewHolder(childView);
            default:

                View genericView = inflater.inflate(R.layout.recycler_view_generic, parent, false);
                return new GenericViewHolder(genericView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        viewHolder.itemView.setSelected(isItemSelected(position));
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
}
