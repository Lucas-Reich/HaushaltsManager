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
import java.util.List;
import java.util.Locale;

public class TabTwoMonthlyReports extends Fragment {

    Calendar cal = Calendar.getInstance();
    List<MonthlyReport> monthlyReports = new ArrayList<>();
    ListView listView;
    MonthlyReportAdapter reportAdapter;
    String TAG = TabTwoMonthlyReports.class.getSimpleName();

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        SharedPreferences preferences = this.getActivity().getSharedPreferences("UserSettings", 0);

        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);
        expensesDataSource = new ExpensesDataSource(getContext());

        listView = (ListView) rootView.findViewById(R.id.booking_listview);

        expensesDataSource.open();

        String startDate = cal.get(Calendar.YEAR) + "-01-01 00:00:00";
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime());

        ArrayList<ExpenseObject> expenses = expensesDataSource.getBookings(startDate, endDate);

        expensesDataSource.close();

        for (int i = 0; i <= cal.get(Calendar.MONTH); i++) {

            monthlyReports.add(new MonthlyReport((i + 1) + "", new ArrayList<ExpenseObject>(), preferences.getString("mainCurrency", "€")));
        }

        for (ExpenseObject expense : expenses) {

            getReports(expense);
        }

        reportAdapter = new MonthlyReportAdapter(monthlyReports, getContext());

        listView.setAdapter(reportAdapter);

        return rootView;
    }

    /**
     * Methode um die Buchung dem richtigen MonthlyReport zuzuweisen.
     * Ist die Buchung eine ParentBuchung, werden statdessen alle Kinden in den MonthlyReport gepackt.
     *
     * @param expense Buchung die einen MonthlyReport zugeordnet werden soll
     */
    private void getReports(ExpenseObject expense) {

        if (!expense.hasChildren()) {

            monthlyReports.get(expense.getDateTime().get(Calendar.MONTH)).addExpense(expense);
        } else {

            for (ExpenseObject child : expense.getChildren()) {

                getReports(child);
            }
        }
    }

    /**
     * Methode um die Ansicht des Tabs beim hinzufügen oder abwählen eines Kontos in ChooseAccountDialogFragment mit neuen Daten zu erneuern
     */
    public void updateView() {

        //todo implement refresh functionality
        throw new UnsupportedOperationException("Die Funktion updateView() in TabTwoMonthlyReports ist noch nicht implementiert!");
    }
}