package com.example.lucas.haushaltsmanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class TabOneBookings extends Fragment{



    ArrayList<ExpenseObject> expenseObjects;
    ListView listView;
    static BookingAdapterVer2 bookingAdapter;

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        expenseObjects = expensesDataSource.getAllBookings();

        bookingAdapter = new BookingAdapterVer2(expenseObjects, getContext());

        expensesDataSource.close();

        listView.setAdapter(bookingAdapter);

        return rootView;
    }
}
