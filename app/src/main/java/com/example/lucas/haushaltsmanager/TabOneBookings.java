package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TabOneBookings extends Fragment {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<ExpenseObject> listDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> listDataChild;
    String TAG = TabOneBookings.class.getSimpleName();

    ExpensesDataSource database;

    FloatingActionButton fab, fabDelete, fabCombine;
    Animation test, fabClose, rotateForward, rotateBackward;
    boolean combOpen = false, delOpen = false, fabOpen = false;


    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne indicators zu machen
     */

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        //View rootView = inflater.inflate(R.layout.activity_test_exp_listview, container, false);
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        //get ListView
        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);

        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        //setting list adapter
        expListView.setAdapter(listAdapter);


        final Activity mainTab = getActivity();
        database = new ExpensesDataSource(getContext());

        // Animated Floating Action Buttons
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (delOpen || combOpen) {

                    listAdapter.deselectAll();
                    closeDelete();
                    closeCombine();
                    closeFab();

                    listAdapter.notifyDataSetChanged();
                } else {

                    Intent intent = new Intent(mainTab, ExpenseScreen.class);
                    mainTab.startActivity(intent);
                }
            }
        });

        fabCombine = (FloatingActionButton) rootView.findViewById(R.id.fab_combine);
        fabCombine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                database.open();
                database.createChildBooking(listAdapter.getSelectedGroupData());
                database.close();
                listAdapter.deselectAll();
                Toast.makeText(mainTab, "Done!", Toast.LENGTH_SHORT).show();
                closeCombine();
                listAdapter.notifyDataSetChanged();
            }
        });

        fabDelete = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                database.open();
                database.deleteBookings(listAdapter.getSelectedBookingIds());
                database.close();
                listAdapter.deselectAll();
                Toast.makeText(mainTab, "Deleted all Bookings", Toast.LENGTH_SHORT).show();
                closeDelete();
                listAdapter.notifyDataSetChanged();
            }
        });


        test = AnimationUtils.loadAnimation(mainTab, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(mainTab, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_backward);


        //OnClickMethods for ExpandableListView

        //ExpandableListView Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                //get expense
                ExpenseObject expense = (ExpenseObject) expListView.getItemAtPosition(groupPosition);
                switch (expense.getAccount().getIndex() + "") {

                    case "9999"://do nothing but expand group

                        return false;
                    case "8888"://do nothing and do not expand group

                        return true;
                    default://open expenseScreen with expense to edit this

                        Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                        openExpense.putExtra("parentExpense", expense.getIndex());
                        startActivity(openExpense);
                        return true;
                }
            }
        });


        //ExpandableListView Child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //get expense
                Object o = expListView.getItemAtPosition(groupPosition);
                ExpenseObject expense = (ExpenseObject) o;

                //start expenseScreen with selected expense
                Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                openExpense.putExtra("childExpense", expense.getIndex());
                startActivity(openExpense);
                return false;
            }
        });

        //ExpandableListView Long click listener for selecting multiple groups
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO datum und group mit kinder dürfen nicht auswählbar sein

                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    if (listAdapter.isSelected(listAdapter.getGroupId(groupPosition))) {

                        listAdapter.removeGroup(listAdapter.getGroupId(groupPosition));
                        view.setBackgroundColor(Color.WHITE);
                    } else {

                        listAdapter.selectGroup(listAdapter.getGroupId(groupPosition));
                        view.setBackgroundColor(Color.GREEN);
                    }

                    animateFabs(listAdapter.getSelectedCount());
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
        database = new ExpensesDataSource(getContext());

        //TODO nur die Buchungen für den aktuellen monat aus der datenbank holen
        database.open();
        Calendar cal = Calendar.getInstance();
        expenses = database.getAllBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));

        String separatorDate = "";

        for (int i = 0; i < expenses.size(); ) {

            if (!expenses.get(i).getDate().equals(separatorDate)) {

                separatorDate = expenses.get(i).getDate();

                Category category = new Category(null, "test", "#000000", false);
                Account account = new Account(8888, "", 0, new Currency("", "", ""));

                ExpenseObject dateSeparator = new ExpenseObject(-1, "", 0, expenses.get(i).getDateTime(), true, category, null, account, null);

                listDataHeader.add(dateSeparator);
                listDataChild.put(dateSeparator, new ArrayList<ExpenseObject>());
            } else {

                listDataHeader.add(expenses.get(i));
                listDataChild.put(expenses.get(i), expenses.get(i).getChildren());
                i++;
            }
        }

        database.close();
    }

    /**
     * animating the FloatingActionButtons
     * <p>
     * TODO die ganzen animations methoden noch einmal neu schreiben da ich mit den aktuellen nicht zufrieden bin
     *
     * @param selectedCount number of selected entries
     */
    private void animateFabs(int selectedCount) {

        switch (selectedCount) {

            case 0:
                closeCombine();
                closeDelete();
                closeFab();
                break;
            case 1:
                openDelete();
                closeCombine();
                openFab();
                break;
            default:
                openCombine();
                openDelete();
                openFab();
                break;
        }
    }

    public void openFab() {

        if (!fabOpen) {

            fab.startAnimation(rotateForward);
            fabOpen = true;
        }
    }

    public void closeFab() {

        if (fabOpen) {

            fab.startAnimation(rotateBackward);
            fabOpen = false;
        }
    }

    public void openDelete() {

        if (!delOpen) {

            fabDelete.startAnimation(test);
            fabDelete.setClickable(true);

            delOpen = true;
        }
    }

    public void closeDelete() {

        if (delOpen) {

            fabDelete.startAnimation(fabClose);
            fabDelete.setClickable(false);

            delOpen = false;
        }
    }

    public void openCombine() {

        if (!combOpen) {

            fabCombine.startAnimation(test);
            fabCombine.setClickable(true);

            combOpen = true;
        }
    }

    public void closeCombine() {

        if (combOpen) {

            fabCombine.startAnimation(fabClose);
            fabCombine.setClickable(false);

            combOpen = false;
        }
    }

    public void updateExpListView() {

        listAdapter.notifyDataSetChanged();
    }
}