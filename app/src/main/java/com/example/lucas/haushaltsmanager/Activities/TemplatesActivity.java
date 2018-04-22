package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.BookingAdapter;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class TemplatesActivity extends AppCompatActivity {
    private static String TAG = TemplatesActivity.class.getSimpleName();

    ArrayList<ExpenseObject> mExpenses;
    ListView mListView;
    Toolbar mToolbar;
    ImageButton mBackArrow;

    ExpensesDataSource mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_bookings);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mListView = (ListView) findViewById(R.id.booking_listview);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO wenn der user nicht vom ExpenseScreen kommt soll diese funktionalität nicht bestehen
                Intent returnTemplateIntent = new Intent();
                returnTemplateIntent.putExtra("templateObj", mExpenses.get(position));
                setResult(Activity.RESULT_OK, returnTemplateIntent);
                finish();
            }
        });
        updateListView();
    }

    private void updateListView() {
        prepareListData();

        BookingAdapter bookingAdapter = new BookingAdapter(mExpenses, this);

        mListView.setAdapter(bookingAdapter);

        bookingAdapter.notifyDataSetChanged();
    }

    private void prepareListData() {

        Log.d(TAG, "prepareListData: Initialisiere Templateliste");
        mExpenses = mDatabase.getTemplates();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mDatabase.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
