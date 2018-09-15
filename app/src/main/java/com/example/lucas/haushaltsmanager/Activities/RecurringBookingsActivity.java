package com.example.lucas.haushaltsmanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        initializeToolbar();

        mListView = (ListView) findViewById(R.id.booking_listview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
