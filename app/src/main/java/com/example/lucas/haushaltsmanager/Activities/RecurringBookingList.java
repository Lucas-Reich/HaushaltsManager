package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.BookingAdapter;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;
import java.util.List;

public class RecurringBookingList extends AbstractAppCompatActivity {
    private static final String TAG = RecurringBookingList.class.getSimpleName();

    private List<ExpenseObject> mRecurringBookings;
    private ListView mListView;
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    private RecurringBookingRepository mRecurringBookingRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        initializeToolbar();

        mListView = findViewById(R.id.booking_list_view);
        mListView.setEmptyView(findViewById(R.id.empty_list_view));

        mRecurringBookingRepo = new RecurringBookingRepository(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();
    }

    private void updateListView() {

        prepareListData();

        BookingAdapter bookingAdapter = new BookingAdapter(mRecurringBookings, this);

        mListView.setAdapter(bookingAdapter);

        bookingAdapter.notifyDataSetChanged();
    }

    private void prepareListData() {

        mStartDate.set(mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), 1);
        mEndDate.set(mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        mRecurringBookings = mRecurringBookingRepo.getAll(mStartDate, mEndDate);
    }
}
