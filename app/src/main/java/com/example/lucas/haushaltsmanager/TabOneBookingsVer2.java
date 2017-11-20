package com.example.lucas.haushaltsmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TabOneBookingsVer2 extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView listView;
    List<ExpenseObject> listDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> listDataChild;

    ExpensesDataSource expensesDataSource;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.activity_test_exp_listview, container, false);

        //get ListView
        listView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        //setting list adapter
        listView.setAdapter(listAdapter);


        //OnClickMethods

        //ListView Group click listener
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                //get expense
                Object o = listView.getItemAtPosition(groupPosition);
                ExpenseObject expense = (ExpenseObject) o;

                //start ExpenseScreen with selected Expense only when the expense has no children
                if (!expense.hasChildren()) {

                    Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                    openExpense.putExtra("parentExpense", expense.getIndex());
                    startActivity(openExpense);
                }
                return false;
            }
        });


        //ListView Child click listener
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //get expense
                Object o = listView.getItemAtPosition(groupPosition);
                ExpenseObject expense = (ExpenseObject) o;

                //start expenseScreen with selected expense
                Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                openExpense.putExtra("childExpense", expense.getIndex());
                startActivity(openExpense);
                return false;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                MainActivityTab test = (MainActivityTab) getActivity();


                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    if (listAdapter.isSelected(listAdapter.getGroupId(groupPosition))) {

                        listAdapter.removeGroup(listAdapter.getGroupId(groupPosition));
                        view.setBackgroundColor(Color.WHITE);
                    } else {

                        listAdapter.setGroupSelected(listAdapter.getGroupId(groupPosition));
                        view.setBackgroundColor(Color.GREEN);
                    }

                    Toast.makeText(test, "" + listAdapter.getSelectedCount(), Toast.LENGTH_SHORT).show();

                    animateFabs(test, listAdapter.getSelectedCount());
                    return true;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {


                    Toast.makeText(getContext(), "CHILD", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

        return rootView;
    }

    /**
     * Preparing the list Data
     */
    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        ArrayList<ExpenseObject> expenses;
        expensesDataSource = new ExpensesDataSource(getContext());

        expensesDataSource.open();
        Calendar cal = Calendar.getInstance();
        expenses = expensesDataSource.getAllBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));

        //assigning child/s to expenses
        for (ExpenseObject expense : expenses) {

            listDataHeader.add(expense);
            listDataChild.put(expense, expense.getChildren());
            //listDataChild.put(expense, expensesDataSource.getChildsToParent(expense.getIndex()));
        }

        expensesDataSource.close();
    }

    /**
     * animating the FloatingActionButtons
     *
     * @param activity parent activity
     */
    private void animateFabs(MainActivityTab activity, int selectedCount) {

        switch (selectedCount) {

            case 0:
                activity.closeCombine();
                activity.closeDelete();
                activity.closeFab();
                break;
            case 1:
                activity.openDelete();
                activity.closeCombine();
                activity.openFab();
                break;
            default:
                activity.openCombine();
                activity.openDelete();
                activity.openFab();
                break;
        }
    }
}