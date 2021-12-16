package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.BookingListInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewSelectedItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules.ExpenseListSelectionRules;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItemViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentExpenseViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem.RecurringBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem.RecurringBookingViewHolder;

import java.util.List;

public class ExpenseListRecyclerViewAdapter extends RecyclerViewSelectedItemHandler {
    public ExpenseListRecyclerViewAdapter(List<IRecyclerItem> items) {
        super(items, new BookingListInsertStrategy(), new ExpenseListSelectionRules());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case DateItem.VIEW_TYPE:

                View dateView = inflater.inflate(R.layout.recycler_view_date, parent, false);
                return new DateViewHolder(dateView);
            case ExpenseItem.VIEW_TYPE:

                View expenseView = inflater.inflate(R.layout.recycler_view_child_expense, parent, false);
                return new ExpenseItemViewHolder(expenseView);
            case ParentBookingItem.VIEW_TYPE:

                View parentExpenseView = inflater.inflate(R.layout.recycler_view_parent_expense, parent, false);
                return new ParentExpenseViewHolder(parentExpenseView);
            case ChildExpenseItem.VIEW_TYPE:

                View childExpenseView = inflater.inflate(R.layout.recycler_view_child, parent, false);
                return new ChildExpenseViewHolder(childExpenseView);
            case RecurringBookingItem.VIEW_TYPE:

                View recurringBookingView = inflater.inflate(R.layout.recycler_view_child_expense, parent, false);
                return new RecurringBookingViewHolder(recurringBookingView);
            default:

                return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        IRecyclerItem item = get(position);

        holder.itemView.setActivated(isSelected(item));
        holder.bind(item);
    }
}
