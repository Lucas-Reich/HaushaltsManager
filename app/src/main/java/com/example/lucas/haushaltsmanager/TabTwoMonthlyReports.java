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

public class TabTwoMonthlyReports extends Fragment {


    ArrayList<MonthlyReport> monthlyReports = new ArrayList<>();
    MonthlyReport[] monthlyReportsv2 = new MonthlyReport[12];
    ListView listView;
    static MonthlyReportAdapter reportAdapter;
    String TAG = "TabTwoMonthlyReports";
    String MAINCURRENCY = "€"; //TODO muss sich selber die Hauptwährung aus der UserSettings Datenbank holen

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);

        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        String startDate = "2017-01-01";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String endDate = dateFormat.format(date);

        ArrayList<ExpenseObject> expenses = expensesDataSource.getAllBookings(startDate, endDate);

        expensesDataSource.close();

        ArrayList<ExpenseObject> tempBookingList = new ArrayList<>();

        int currentMonth = 1;
/*
        for (int index = 0; index < expenses.size(); index++) {

            ExpenseObject current = expenses.get(index);
            //String currentMonth = current.getDate().split("-")[1];
            //int nextMonth = Integer.parseInt(expenses.get(index + 1).getDate().split("-")[1]) == currentMonth;

            //if ((index + 1) == expenses.size() || !expenses.get(index+1).getDate().split("-")[1].equals(currentMonth)) {
            if ((index + 1) == expenses.size() || Integer.parseInt(expenses.get(index + 1).getDate().split("-")[1]) != currentMonth) {

                monthlyReports.add(new MonthlyReport((currentMonth + ""), tempBookingList, MAINCURRENCY));
                tempBookingList.clear();
                currentMonth++;
            } else {

                tempBookingList.add(current);
            }
        }
*/
        for (int index = 0; index < expenses.size(); ) { //TODO die monthlyReports noch einmal neu zusammenfassen, da ich mit der aktuellen version nicht zufrieden bin

            ExpenseObject current = expenses.get(index);

            try {

                // wenn die nächste buchung noch im gleichen monat gemacht wurde
                if (current.getDate().split("-")[1].equals(expenses.get(index + 1).getDate().split("-")[1])) {

                    tempBookingList.add(current);
                    index++;
                    // wenn im aktuellen monat keine buchung gemacht wurde
                } else if (Integer.parseInt(current.getDate().split("-")[1]) != currentMonth) {

                    monthlyReports.add(new MonthlyReport((currentMonth + ""), tempBookingList, MAINCURRENCY));
                    tempBookingList.clear();
                    currentMonth++;
                } else {

                    tempBookingList.add(current);
                    //die tempBookingList muss als neue liste übergeben werden, da sie sonst als pointer übergeben wir und alle buchungslisten immer den gleichen wert annehmen!!!
                    monthlyReports.add(new MonthlyReport((currentMonth + ""), new ArrayList<>(tempBookingList), MAINCURRENCY));

                    tempBookingList.clear();
                    currentMonth++;
                    index++;
                }
            } catch (IndexOutOfBoundsException e) {

                // wenn die aktuelle buchung die letzte ist, aber vor dieser buchung ein monat ist indem keine buchungen gemacht wurden
                if (Integer.parseInt(current.getDate().split("-")[1]) != currentMonth) {

                    monthlyReports.add(new MonthlyReport((currentMonth + ""), tempBookingList, MAINCURRENCY));
                    tempBookingList.clear();
                    currentMonth++;
                } else {

                    //wenn die aktuelle buchung die letzte ist
                    tempBookingList.add(current);
                    monthlyReports.add(new MonthlyReport((currentMonth + ""), tempBookingList, MAINCURRENCY));
                    break;
                }
            }
        }





/*
        for (int index = 0; index < expenses.size(); ) { //TODO die monthlyReports noch einmal neu zusammenfassen, da ich mit der aktuellen version nicht zufrieden bin

            ExpenseObject current = expenses.get(index);

            try {

                // wenn die nächste buchung noch im gleichen monat gemacht wurde
                if (current.getDate().split("-")[1].equals(expenses.get(index + 1).getDate().split("-")[1])) {

                    tempBookingList.add(current);
                    index++;
                    // wenn im aktuellen monat keine buchung gemacht wurde
                } else if (Integer.parseInt(current.getDate().split("-")[1]) != currentMonth) {

                    monthlyReportsv2[currentMonth - 1] = new MonthlyReport(currentMonth + "", tempBookingList, MAINCURRENCY);
                    tempBookingList.clear();
                    currentMonth++;
                } else {

                    tempBookingList.add(current);
                    monthlyReportsv2[currentMonth - 1] = new MonthlyReport(currentMonth + "", tempBookingList, MAINCURRENCY);
                    tempBookingList.clear();
                    currentMonth++;
                    index++;
                }
            } catch (IndexOutOfBoundsException e) {

                // wenn die aktuelle buchung die letzte ist, aber vor dieser buchung ein monat ist indem keine buchungen gemacht wurden
                if (Integer.parseInt(current.getDate().split("-")[1]) != currentMonth) {

                    monthlyReportsv2[currentMonth - 1] = new MonthlyReport(currentMonth + "", tempBookingList, MAINCURRENCY);
                    tempBookingList.clear();
                    currentMonth++;
                } else {

                    //wenn die aktuelle buchung die letzte ist
                    tempBookingList.add(current);
                    monthlyReportsv2[currentMonth - 1] = new MonthlyReport(currentMonth + "", tempBookingList, MAINCURRENCY);
                    break;
                }
            }
        }
*/





















        reportAdapter = new MonthlyReportAdapter(monthlyReports, getContext());

        listView.setAdapter(reportAdapter);

        return rootView;
    }
}