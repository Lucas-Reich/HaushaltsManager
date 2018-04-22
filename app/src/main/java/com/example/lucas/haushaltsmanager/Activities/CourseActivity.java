package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;

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

    private ImageButton mBackArrow;
    private Toolbar mToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mExpenses = new ArrayList<>();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
        //implementiere einen callback in der expandableListeView die die activity benachrichtig, wenn neue daten geladen werden müssen
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }
}
