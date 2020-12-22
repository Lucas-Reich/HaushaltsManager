package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.RecurringBookingItem.RecurringBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingList extends AbstractAppCompatActivity {
    private RecurringBookingRepository mRecurringBookingRepo;
    private RecyclerView mRecyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        initializeToolbar();

        mRecyclerView = findViewById(R.id.recurring_bookings_rec_view);
        mRecyclerView.setLayoutManager(LayoutManagerFactory.vertical(this));

        mRecurringBookingRepo = new RecurringBookingRepository(this);
    }

    private void updateListView() {
        ExpenseListRecyclerViewAdapter mAdapter = new ExpenseListRecyclerViewAdapter(loadData());

        mRecyclerView.setAdapter(mAdapter);
    }

    private List<IRecyclerItem> loadData() {
        List<RecurringBooking> recurringBookings = mRecurringBookingRepo.getAll(
                getFirstOfMonth(),
                getLastOfMonth()
        );

        return transform(recurringBookings);
    }

    private List<IRecyclerItem> transform(List<RecurringBooking> recurringBookings) {
        List<IRecyclerItem> items = new ArrayList<>();
        for (RecurringBooking recurringBooking : recurringBookings) {
            items.add(new RecurringBookingItem(recurringBooking));
        }

        return items;
    }

    private Calendar getFirstOfMonth() {
        Calendar date = Calendar.getInstance();
        date.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                1
        );

        return date;
    }

    private Calendar getLastOfMonth() {
        Calendar date = Calendar.getInstance();
        date.set(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.getActualMaximum(Calendar.DAY_OF_MONTH)
        );

        return date;
    }
}
