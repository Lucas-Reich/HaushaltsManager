package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.BookingAdapter;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;
import java.util.List;

public class RecurringBookingsActivity extends AppCompatActivity {
    private static final String TAG = RecurringBookingsActivity.class.getSimpleName();

    private List<ExpenseObject> mRecurringBookings;
    private ListView mListView;
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    private ImageButton mBackArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mListView = (ListView) findViewById(R.id.booking_listview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
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

        mRecurringBookings = RecurringBookingRepository.getAll(mStartDate, mEndDate);
    }

    public void setStartDate(long startInMills) {

        this.mStartDate.setTimeInMillis(startInMills);
    }

    public void setEndDate(long endInMills) {

        this.mEndDate.setTimeInMillis(endInMills);
    }
}
