package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecurringBookingAdapter;

import java.util.Calendar;
import java.util.List;

public class RecurringBookingList extends AbstractAppCompatActivity {
    private ExpandableListView mExpListView;
    private RecurringBookingRepository mRecurringBookingRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        initializeToolbar();

        mExpListView = findViewById(R.id.booking_list_view);
        mExpListView.setEmptyView(findViewById(R.id.empty_list_view));

        mRecurringBookingRepo = new RecurringBookingRepository(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();
    }

    private void updateListView() {
        RecurringBookingAdapter recurringBookingAdapter = new RecurringBookingAdapter(
                this,
                getRecurringBookings()
        );

        mExpListView.setAdapter(recurringBookingAdapter);

        recurringBookingAdapter.notifyDataSetChanged();
    }

    private List<RecurringBooking> getRecurringBookings() {
        return mRecurringBookingRepo.getAll(
                getFirstOfMonth(),
                getLastOfMonth()
        );
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
