package com.example.lucas.haushaltsmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TabTwoMonthlyReports extends Fragment {


    Calendar cal = Calendar.getInstance();
    MonthlyReport[] monthlyReports = new MonthlyReport[12];
    ListView listView;
    static MonthlyReportAdapter reportAdapter;
    String TAG = "TabTwoMonthlyReports";

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        SharedPreferences preferences = this.getActivity().getSharedPreferences("UserSettings", 0);

        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);
        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        String startDate = cal.get(Calendar.YEAR) + "-01-01 00:00:00";
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime());

        ArrayList<ExpenseObject> expenses = expensesDataSource.getAllBookings(startDate, endDate);

        expensesDataSource.close();
        //das array mit leeren MonthlyReports befüllen TODO nur mit so vielen wie es aktuell monate gibt
        for (int i = 0; i < monthlyReports.length; i++) {

            monthlyReports[i] = new MonthlyReport((i + 1) + "", new ArrayList<ExpenseObject>(), preferences.getString("mainCurrency", "€"));
        }

        for (ExpenseObject expense : expenses) {

            getReports(expense);
        }

        reportAdapter = new MonthlyReportAdapter(monthlyReports, getContext());

        listView.setAdapter(reportAdapter);

        return rootView;
    }

    private void getReports(ExpenseObject expense) {

        if (!expense.hasChildren()) {

            monthlyReports[expense.getDate().get(Calendar.MONTH)].addExpense(expense);
        } else {

            for (ExpenseObject child : expense.getChildren()) {

                getReports(child);
            }
        }
    }
}