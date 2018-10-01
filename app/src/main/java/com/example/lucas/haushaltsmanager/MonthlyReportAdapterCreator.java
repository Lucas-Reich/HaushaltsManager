package com.example.lucas.haushaltsmanager;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Reports.MonthlyReport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthlyReportAdapterCreator {
    private static final String TAG = MonthlyReportAdapterCreator.class.getSimpleName();

    private List<ExpenseObject> mExpenses;
    private Context mContext;
    private List<MonthlyReport> mReports;
    private List<Long> mActiveAccounts;

    public MonthlyReportAdapterCreator(List<ExpenseObject> expenses, Context context, List<Long> activeAccounts) {

        mActiveAccounts = activeAccounts;
        mContext = context;
        mExpenses = expenses;
        mReports = new ArrayList<>();
    }

    /**
     * Methode um einen MonthlyReportAdapter zu erstellen, mit den aktuellen Daten
     *
     * @return MonthlyReportAdapter
     */
    public MonthlyReportAdapter getAdapter() {
        createMonthlyReports();

        return new MonthlyReportAdapter(mContext, mReports);
    }


    /**
     * Methode um die MonthlyReports zu erstellen.
     */
    private void createMonthlyReports() {

        for (int i = getCurrentMonth(); i >= 1; i--) {
            mReports.add(new MonthlyReport(i + "", new ArrayList<ExpenseObject>(), getMainCurrency(), mContext));
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
    private Currency getMainCurrency() {
        UserSettingsPreferences preferences = new UserSettingsPreferences(mContext);

        return preferences.getMainCurrency();
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
        mReports.get(mReports.size() - expenseMonth - 1).addExpense(expense);
    }

    /**
     * Methode um zu überprüfen, ob die angegebene Buchung angezeigt werden soll oder nicht
     *
     * @param expense Zu überprüfende Buchung
     * @return Boolean
     */
    private Boolean isExpenseVisible(ExpenseObject expense) {
        return mActiveAccounts.contains(expense.getAccountId());
    }
}
