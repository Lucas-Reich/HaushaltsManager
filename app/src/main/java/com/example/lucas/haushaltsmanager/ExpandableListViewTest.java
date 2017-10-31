package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpandableListViewTest extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<ExpenseObject> listDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> listDataChild;

    ExpensesDataSource expensesDataSource;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_exp_listview);

        //get ListView
        listView = (ExpandableListView) findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        //setting list adapter
        listView.setAdapter(listAdapter);


        //OnClickMethods

        //ListView Group click listener
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                //TODO create onClickListener functionality
                return false;
            }
        });

        //ListView Group expanded listener
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                //TODO create onClickListener functionality
            }
        });

        //ListView Group collapsed listener
        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                //TODO create onClickListener functionality
            }
        });

        //ListView Child click listener
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //TODO create onClickListener functionality
                return false;
            }
        });
    }

    /**
     * Preparing the list Data
     */
    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        ArrayList<ExpenseObject> expenses;
        expensesDataSource = new ExpensesDataSource(this);

        expensesDataSource.open();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        expenses = expensesDataSource.getAllBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));

        //assigning child/s to expenses
        for (ExpenseObject expense : expenses) {

            listDataHeader.add(expense);
            listDataChild.put(expense, expense.getChildren());
        }

        expensesDataSource.close();
    }
}
