package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MonthlyReportAdapterCreator {

    private String TAG = MonthlyReportAdapterCreator.class.getSimpleName();

    private ArrayList<ExpenseObject> mExpenses;
    private Context mContext;
    private List<MonthlyReport> mMonthlyReports;
    private ArrayList<Long> mActiveAccounts;

    public MonthlyReportAdapterCreator(ArrayList<ExpenseObject> expenses, Context context, ArrayList<Long> activeAccounts) {

        mActiveAccounts = activeAccounts;
        mContext = context;
        mExpenses = expenses;
        mMonthlyReports = new ArrayList<>();
    }

    /**
     * Methode um einen MonthlyReportAdapter zu erstellen, mit den aktuellen Daten
     *
     * @return MonthlyReportAdapter
     */
    public MonthlyReportAdapter getAdapter() {
        createMonthlyReports();

        return new MonthlyReportAdapter(mMonthlyReports, mContext);
    }


    /**
     * Methode um die MonthlyReports zu erstellen.
     */
    private void createMonthlyReports() {

        for (int i = 1; i <= getCurrentMonth(); i++) {

            mMonthlyReports.add(new MonthlyReport(i + "", new ArrayList<ExpenseObject>(), getMainCurrency()));
        }

        fillMonthlyReports();
    }

    /**
     * Methode um den aktuellen Monat als nicht 0 basierten Integer zu erhalten
     *
     * @return Aktueller Monat als int
     */
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();

        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * Methode um die Hauptwährung des Users aus den ScharedPreferences auszulesen
     *
     * @return Währungssymbol der Hauptwährung
     */
    private String getMainCurrency() {
        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        return preferences.getString("mainCurrency", "€");
    }

    /**
     * Methode um die leeren MonthlyReports mit den Buchungen aus mExpenses zu befüllen
     */
    private void fillMonthlyReports() {

        for (ExpenseObject expense : mExpenses) {

            assignBookingToReport(expense);
        }
    }

    /**
     * Methode um die Buchung dem richtigen MonthlyReport zuzuweisen.
     * Ist die Buchung eine ParentBuchung, werden statdessen alle Kinden in den MonthlyReport gepackt.
     * Ist das Konto der Buchung nicht in der aktiven Konotliste aufgeführt wird die Buchung nicht mit im MonthlyReport einbezogen.
     *
     * @param expense Buchung die einen MonthlyReport zugeordnet werden soll
     */
    private void assignBookingToReport(ExpenseObject expense) {

        if (expense.isParent()) {

            for (ExpenseObject child : expense.getChildren()) {

                assignBookingToReport(child);
            }
        } else {

            if (isExpenseVisible(expense))
                addExpenseToReport(expense);
        }
    }

    /**
     * Methode um eine Ausgabe dem richtigen MonthlyReport zuzuordnen
     *
     * @param expense Ausgabe die zugeordnet werden soll
     */
    private void addExpenseToReport(ExpenseObject expense) {

        int expenseMonth = expense.getDateTime().get(Calendar.MONTH);
        mMonthlyReports.get(expenseMonth).addExpense(expense);
    }

    /**
     * Methode um zu überprüfen, ob die angegebene Buchung angezeigt werden soll oder nicht
     *
     * @param expense Zu überprüfende Buchung
     * @return Boolean
     */
    private Boolean isExpenseVisible(ExpenseObject expense) {

        return mActiveAccounts.contains(expense.getAccount().getIndex());
    }
}
