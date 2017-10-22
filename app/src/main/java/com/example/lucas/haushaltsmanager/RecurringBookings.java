package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class RecurringBookings extends AppCompatActivity {


    ArrayList<ExpenseObject> expenseObjects;
    ListView listView;
    static BookingAdapterVer2 bookingAdapter;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();

    ExpensesDataSource expensesDataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_bookings);

        //TODO make date range variable
        startDate.set(2017, 10, 1);
        endDate.set(2017, 10, 31);


        expensesDataSource = new ExpensesDataSource(this);

        listView = (ListView) findViewById(R.id.booking_listview);

        expensesDataSource.open();

        expenseObjects = expensesDataSource.getRecurringBookings(startDate, endDate);

        bookingAdapter = new BookingAdapterVer2(expenseObjects, this);

        expensesDataSource.close();

        listView.setAdapter(bookingAdapter);
    }
}
