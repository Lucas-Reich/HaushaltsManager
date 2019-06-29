package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.ExpenseListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewSelectedItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.ExpenseListSelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.RecurringBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.AdViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ChildExpenseViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.DateViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ExpenseItemViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.GenericViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.ParentExpenseViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.ViewHolder.RecurringBookingViewHolder;

import java.util.List;

public class ExpenseListRecyclerViewAdapter extends RecyclerViewSelectedItemHandler {
    public ExpenseListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new ExpenseListInsertStrategy(), new ExpenseListSelectionRules());
    }

    // TODO Der GenericViewHolder sollte hier in dem Adapter definiert werden

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case DateItem.VIEW_TYPE:

                View dateView = inflater.inflate(R.layout.recycler_view_date, parent, false);
                return new DateViewHolder(dateView);
            case ExpenseItem.VIEW_TYPE:

                View expenseView = inflater.inflate(R.layout.recycler_view_child_expense, parent, false);
                return new ExpenseItemViewHolder(expenseView);
            case ParentExpenseItem.VIEW_TYPE:

                View parentExpenseView = inflater.inflate(R.layout.recycler_view_parent_expense, parent, false);
                return new ParentExpenseViewHolder(parentExpenseView);
            case AdItem.VIEW_TYPE:

                View AdView = inflater.inflate(R.layout.recycler_view_child_expense, parent, false);
                return new AdViewHolder(AdView);
            case ChildExpenseItem.VIEW_TYPE:

                View childExpenseView = inflater.inflate(R.layout.recycler_view_child, parent, false);
                return new ChildExpenseViewHolder(childExpenseView);
            case RecurringBookingItem.VIEW_TYPE:

                View recurringBookingView = inflater.inflate(R.layout.recycler_view_child_expense, parent, false);
                return new RecurringBookingViewHolder(recurringBookingView);
            default:

                View genericView = inflater.inflate(R.layout.recycler_view_generic, parent, false);
                return new GenericViewHolder(genericView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IRecyclerItem item = getItem(position);

        AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        viewHolder.itemView.setSelected(isItemSelected(item));
        viewHolder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }
}
