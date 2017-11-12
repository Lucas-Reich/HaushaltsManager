package com.example.lucas.haushaltsmanager;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = listView.getItemAtPosition(position);
                ExpenseObject test = (ExpenseObject) o;
                Intent openExpense = new Intent(getActivity(), ExpenseScreen.class);
                openExpense.putExtra("index", test.getIndex());
                startActivity(openExpense);
            }
        });

        return rootView;
    }
}
