package com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryRepository;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerViewItemHandler;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.AbstractViewHolder;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem.RecurringBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem.RecurringBookingViewHolder;

import java.util.List;

public class RecurringBookingsViewAdapter extends RecyclerViewItemHandler {
    public RecurringBookingsViewAdapter(List<IRecyclerItem> items) {
        super(items, new AppendInsertStrategy());
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CategoryRepository categoryRepository = new CategoryRepository(AppDatabase.getDatabase(parent.getContext()).categoryDAO());

        if (viewType == RecurringBookingItem.VIEW_TYPE) {
            return new RecurringBookingViewHolder(inflater.inflate(
                    R.layout.recycler_view_child_expense,
                    parent,
                    false
            ), categoryRepository);
        }

        return super.onCreateViewHolder(parent, viewType);
    }
}
