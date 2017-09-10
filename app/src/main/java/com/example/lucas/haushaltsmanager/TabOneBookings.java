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
    static BookingAdapter adapter;
    static BookingAdapterVer2 adapterVer2;

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        expenseObjects = expensesDataSource.getAllBookings();
/*
        adapter = new BookingAdapter(expenseObjects, getContext());

        listView.setAdapter(adapter);
*/
        adapterVer2 = new BookingAdapterVer2(expenseObjects, getContext());

        listView.setAdapter(adapterVer2);

        return rootView;
    }
}
