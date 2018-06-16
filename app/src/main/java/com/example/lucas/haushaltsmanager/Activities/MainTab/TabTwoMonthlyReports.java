package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.MonthlyReportAdapterCreator;
import com.example.lucas.haushaltsmanager.MonthlyReportAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class TabTwoMonthlyReports extends Fragment {
    private static final String TAG = TabTwoMonthlyReports.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ArrayList<Long> mActiveAccounts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActiveAccounts = new ArrayList<>();
        setActiveAccounts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_two_monthly_reports, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tab_two_recycler_view);

        updateExpandableListView();

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

            if (preferences.getBoolean(account.getTitle(), false))
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
    public void updateExpandableListView() {

        MonthlyReportAdapter adapter = new MonthlyReportAdapterCreator(getExpenses(), getContext(), mActiveAccounts).getAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
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
