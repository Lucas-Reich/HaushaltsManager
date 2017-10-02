package com.example.lucas.haushaltsmanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class TabTwo extends Fragment{



    ArrayList<ExpenseObject> expenseObjects;
    ListView listView;
    static MonthlyOverviewAdapter adapter;

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_two_, container, false);

        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        expensesDataSource.close();

        expenseObjects = new ArrayList<>();

        adapter = new MonthlyOverviewAdapter(expenseObjects, getContext());

        listView.setAdapter(adapter);

        return rootView;
    }
}