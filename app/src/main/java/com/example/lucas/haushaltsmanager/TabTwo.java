package com.example.lucas.haushaltsmanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TabTwo extends Fragment {


    ArrayList<MonthlyReport> monthlyReports;
    ListView listView;
    static MonthlyOverviewAdapter reportAdapter;

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_two_, container, false);

        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        String startDate = "2017-01-01";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String endDate = dateFormat.format(date);
        // String endDate = "2017-06-01"; //TODO change to current date -- done

        ArrayList<ExpenseObject> expenses = expensesDataSource.getAllBookings(startDate, endDate);

        expensesDataSource.close();

        ArrayList<ExpenseObject> monthlyBookingList = new ArrayList<>();

        //TODO wenn sich ein monat in dem gewählten zeitraum befindet der keine einträge hat, dann wird dieser trotzdem in dem monthlyReport vermekrt
        //TODO darf er aber nicht, weil keine Eintröge vorhanden sind und die anzeige somit fehlerhaft ist--ändern!!
        String currentMonth = startDate.substring(5,6);

        for (int i = 0; i < expenses.size(); i++) {

            if (currentMonth.equals(expenses.get(i).getDate().substring(5,6))) {

                monthlyBookingList.add(expenses.get(i));
            } else {

                monthlyReports.add(new MonthlyReport(currentMonth, monthlyBookingList, "", "€"));
                monthlyBookingList.clear();

                monthlyBookingList.add(expenses.get(i));
                currentMonth = expenses.get(i).getDate().substring(5, 6);
            }
        }

        reportAdapter = new MonthlyOverviewAdapter(monthlyReports, getContext());

        listView.setAdapter(reportAdapter);

        return rootView;
    }
}