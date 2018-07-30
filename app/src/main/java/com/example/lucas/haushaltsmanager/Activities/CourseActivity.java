package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity {
    private static final String TAG = CourseActivity.class.getSimpleName();

    private List<ExpenseObject> mExpenses;
    private List<Long> mActiveAccounts;
    private ExpandableListView mExpListView;
    private ImageButton mBackArrow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mExpListView = (ExpandableListView) findViewById(R.id.lvExp);
        mExpenses = new ArrayList<>();
        mActiveAccounts = new ArrayList<>();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        updateExpListView();
    }

    private void updateExpListView() {

        prepareDataSources();

        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapterCreator(mExpenses, mActiveAccounts, this).getExpandableListAdapter();

        mExpListView.setAdapter(expandableListAdapter);

        expandableListAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um die Ausgaben aus der Datenbank zu laden.
     */
    private void prepareDataSources() {

        Log.d(TAG, "prepareDataSources: Initialisiere die Buchungsliste");
        mExpenses = ExpenseRepository.getAll();

        prepareAccountData();
    }

    /**
     * Methode um die Id's der aktiven Konten zu laden.
     */
    private void prepareAccountData() {

        Log.d(TAG, "prepareAccountData: Initialisiere Kontenliste");
        List<Account> accounts = AccountRepository.getAll();
        for (Account account : accounts) {
            mActiveAccounts.add(account.getIndex());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);

        return true;
    }

    /**
     * Methodem die aufgereufen wird wenn der user auf ein Menüelement klickt.
     * Dabei hat er die Auswahl zwischen 'Sortieren' und 'Filtern'
     *
     * @param item angeklicktes Menüelement
     * @return Boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.course_menu_sort:
                //todo zeige einen AlertDialog an in dem sortier optionen (Datum, Betrag, Kategorie, Alphabetisch) angezeigt werden
                Toast.makeText(this, "Huch das wurde wohl noch nicht implementiert", Toast.LENGTH_SHORT).show();
                break;
            case R.id.course_menu_filter:
                //todo zeige einen alertdialog in dem filteroptionen (Ausgabe, Einnahme, Datum, ...) angezeigt werden
                Toast.makeText(this, "Huch das wurde wohl noch nicht implementiert", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new UnsupportedOperationException("Die Menüoption " + item.getItemId() + " wird nicht unterstützt!");
        }

        return true;
    }
}
