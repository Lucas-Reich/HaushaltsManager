package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.BookingAdapter;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.Calendar;

public class RecurringBookingsActivity extends AppCompatActivity {

    private static final String TAG = RecurringBookingsActivity.class.getSimpleName();

    ArrayList<ExpenseObject> mExpenses;
    ListView mListView;
    Calendar mStartDate = Calendar.getInstance();
    Calendar mEndDate = Calendar.getInstance();

    ExpensesDataSource mDatabase;

    Toolbar mToolbar;
    ImageButton mBackArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mListView = (ListView) findViewById(R.id.booking_listview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();

        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void updateListView() {

        prepareListData();

        BookingAdapter bookingAdapter = new BookingAdapter(mExpenses, this);

        mListView.setAdapter(bookingAdapter);

        bookingAdapter.notifyDataSetChanged();
    }

    private void prepareListData() {

        mStartDate.set(mStartDate.get(Calendar.YEAR), mStartDate.get(Calendar.MONTH), 1);
        mEndDate.set(mEndDate.get(Calendar.YEAR), mEndDate.get(Calendar.MONTH), mEndDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        mExpenses = mDatabase.getRecurringBookings(mStartDate, mEndDate);
    }

    public void setStartDate(long startInMills) {

        this.mStartDate.setTimeInMillis(startInMills);
    }

    public void setEndDate(long endInMills) {

        this.mEndDate.setTimeInMillis(endInMills);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }
}
