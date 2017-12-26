package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class BookingTemplates extends AppCompatActivity {


    ArrayList<ExpenseObject> expenseObjects;
    ListView listView;
    static BookingAdapter bookingAdapter;

    private static String TAG = "BookingTemplates";

    ExpensesDataSource expensesDataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_bookings);

        expensesDataSource = new ExpensesDataSource(this);

        listView = (ListView) findViewById(R.id.booking_listview);

        expensesDataSource.open();

        expenseObjects = expensesDataSource.getTemplates();

        bookingAdapter = new BookingAdapter(expenseObjects, this);

        expensesDataSource.close();

        listView.setAdapter(bookingAdapter);
    }
}
