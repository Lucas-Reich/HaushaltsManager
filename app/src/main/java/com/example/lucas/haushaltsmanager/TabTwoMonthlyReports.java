package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    //todo eineige der methoden (setActiveAccounts, refreshListOnAccountSelected, updateExpandableListView) können in eine abstrakte klasse gepackt werden, welche von jedem tab implementiert wird

    //todo immer wenn ich von tab 3 auf tab 2 wechlse dann werden ~0.3 MB Arbeitsspeicher mehr in anspruch genommen als vorher
    //irgendwo muss es eine memory leak geben

    List<MonthlyReport> mMonthlyReports;
    MonthlyReportAdapter mReportAdapter;
    ArrayList<ExpenseObject> mExpenses;
    ArrayList<Long> mActiveAccounts;
    ExpensesDataSource mDatabase;
    ListView mListView;
    Calendar mCal;

    String TAG = TabTwoMonthlyReports.class.getSimpleName();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: fetching data form mDatabase");

        mActiveAccounts = new ArrayList<>();
        mMonthlyReports = new ArrayList<>();
        mExpenses = new ArrayList<>();

        mDatabase = new ExpensesDataSource(getContext());
        mDatabase.open();

        mCal = Calendar.getInstance();

        setActiveAccounts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);

        mListView = (ListView) rootView.findViewById(R.id.booking_listview);

        //updateExpandableListView(); todo enable

        return rootView;
    }

    /**
     * Methode um die MonthlyReports zu erstellen.
     */
    private void createMonthlyReports() {

        SharedPreferences preferences = getContext().getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        if (!mMonthlyReports.isEmpty())
            mMonthlyReports.clear();

        if (mExpenses.isEmpty()) {

            if (!mDatabase.isOpen())
                mDatabase.open();

            String startDate = mCal.get(Calendar.YEAR) + "-01-01 00:00:00";
            String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(mCal.getTime());

            mExpenses = mDatabase.getBookings(startDate, endDate);
        }

        for (int i = 0; i <= mCal.get(Calendar.MONTH); i++) {

            mMonthlyReports.add(new MonthlyReport((i + 1) + "", new ArrayList<ExpenseObject>(), preferences.getString("mainCurrency", "€")));
        }

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

        if (!expense.hasChildren()) {
/*todo enable
            if (!mActiveAccounts.contains(expense.getAccount().getIndex()))
                return;
*/
            mMonthlyReports.get(expense.getDateTime().get(Calendar.MONTH)).addExpense(expense);
        } else {

            for (ExpenseObject child : expense.getChildren()) {

                assignBookingToReport(child);
            }
        }
    }


    /**
     * Methode um die mActiveAccounts liste zu initialisieren
     */
    private void setActiveAccounts() {

        Log.d(TAG, "setActiveAccounts: Erneuere aktive Kontenliste");

        if (!mDatabase.isOpen())
            mDatabase.open();

        SharedPreferences preferences = getContext().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        for (Account account : mDatabase.getAllAccounts()) {

            if (preferences.getBoolean(account.getAccountName().toLowerCase(), false))
                mActiveAccounts.add(account.getIndex());
        }
    }

    /**
     * Methode um die Ansicht des Tabs beim hinzufügen oder abwählen eines Kontos in ChooseAccountDialogFragment mit neuen Daten zu erneuern
     */
    public void refreshListOnAccountSelected(long accountId, boolean isChecked) {

        if (mActiveAccounts.contains(accountId) == isChecked)
            return;

        if (mActiveAccounts.contains(accountId) && !isChecked)
            mActiveAccounts.remove(accountId);
        else
            mActiveAccounts.add(accountId);

        updateExpandableListView();
    }

    /**
     * Methode um die ExpandableListView nach eine Änderung neu anzuzeigen
     */
    //hat performance probleme
    public void updateExpandableListView() {

        createMonthlyReports();

        mReportAdapter = new MonthlyReportAdapter(mMonthlyReports, getContext());

        //mListView.setAdapter(mReportAdapter); macht probleme

        mReportAdapter.notifyDataSetChanged();
    }
}