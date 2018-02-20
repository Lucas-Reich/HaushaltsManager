package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class RecurringBookings extends AppCompatActivity {

    private static final String TAG = RecurringBookings.class.getSimpleName();
    ArrayList<ExpenseObject> mExpenses;
    ListView mListView;
    BookingAdapter mBookingAdapter;
    Calendar mStartDate = Calendar.getInstance();
    Calendar mEndDate = Calendar.getInstance();

    ExpensesDataSource mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        mStartDate.set(mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), 1);
        mEndDate.set(mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mListView = (ListView) findViewById(R.id.booking_listview);

        mExpenses = mDatabase.getRecurringBookings(mStartDate, mEndDate);

        mBookingAdapter = new BookingAdapter(mExpenses, this);

        mListView.setAdapter(mBookingAdapter);
    }

    public void setStartDate(long startInMills) {

        this.mStartDate.setTimeInMillis(startInMills);
    }

    public void setEndDate(long endInMills) {

        this.mEndDate.setTimeInMillis(endInMills);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }
}
