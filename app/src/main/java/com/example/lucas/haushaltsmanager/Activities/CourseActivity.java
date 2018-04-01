package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    private ArrayList<ExpenseObject> mExpenses;
    private ExpensesDataSource mDatabase;

    private ExpandableListView mExpandableListView;
    private ExpandableListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mExpenses = new ArrayList<>();
    }


    @Override
    protected void onStart() {
        super.onStart();

        //implementiere einen callback in der expandableListeView die die activity benachrichtig, wenn neue daten geladen werden müssen
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }
}
