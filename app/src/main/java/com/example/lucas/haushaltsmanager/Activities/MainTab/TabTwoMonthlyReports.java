package com.example.lucas.haushaltsmanager.Activities.MainTab;

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

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.MonthlyReportAdapter;
import com.example.lucas.haushaltsmanager.MonthlyReportAdapterCreator;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class TabTwoMonthlyReports extends Fragment {

    String TAG = TabTwoMonthlyReports.class.getSimpleName();
    //todo eineige der methoden (setActiveAccounts, refreshListOnAccountSelected, updateExpandableListView) können in eine abstrakte klasse gepackt werden, welche von jedem tab implementiert wird

    //todo immer wenn ich von tab 3 auf tab 2 wechlse dann werden ~0.3 MB Arbeitsspeicher mehr in anspruch genommen als vorher

    MonthlyReportAdapter mReportAdapter;
    ArrayList<Long> mActiveAccounts;
    ListView mListView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: fetching data from Database");

        mActiveAccounts = new ArrayList<>();
        setActiveAccounts();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);

        mListView = (ListView) rootView.findViewById(R.id.booking_listview);

        //updateExpandableListView(); todo lässt die anwendung freezen (ca. 50% cpu auslastung)

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Methode um die mActiveAccounts liste zu initialisieren
     */
    private void setActiveAccounts() {

        Log.d(TAG, "setActiveAccounts: Erneuere aktive Kontenliste");

        SharedPreferences preferences = getContext().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        for (Account account : getAllAccounts()) {

            if (preferences.getBoolean(account.getName(), false))
                mActiveAccounts.add(account.getIndex());
        }
    }

    /**
     * Methode um alle verfügbaren Konten aus der Datenbank zu holen
     *
     * @return Liste alles verfügbaren Konten
     */
    private ArrayList<Account> getAllAccounts() {

        ExpensesDataSource database = new ExpensesDataSource(getContext());
        database.open();

        ArrayList<Account> accounts = database.getAllAccounts();
        database.close();

        return accounts;
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

        Log.d(TAG, "updateExpandableListView: erjhnbguileqgbhriutebguoitnhbegwöilehgiohqwetöuighöoleurghqöoleighouregbh");

        mReportAdapter = new MonthlyReportAdapterCreator(getExpenses(), getContext(), mActiveAccounts).getAdapter();

        mListView.setAdapter(mReportAdapter);

        mReportAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um die Ausgaben des aktuellen Jahres aus der Datenbank zu holen
     *
     * @return Liste der Buchungen des aktuellen Jahres
     */
    private ArrayList<ExpenseObject> getExpenses() {

        ExpensesDataSource database = new ExpensesDataSource(getContext());
        database.open();

        ArrayList<ExpenseObject> expenses = database.getBookings();
        database.close();

        return expenses;
    }
}