package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    ExpandableListAdapter mListAdapter;
    ExpandableListView mExpListView;
    List<ExpenseObject> mListDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> mListDataChild;
    String TAG = TabOneBookings.class.getSimpleName();

    ExpensesDataSource database;

    FloatingActionButton fab, fabDelete, fabCombine;
    Animation test, fabClose, rotateForward, rotateBackward;
    boolean combOpen = false, delOpen = false, fabOpen = false;
    boolean mSelectionMode = false;


    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne indicators zu machen
     */

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        //get ListView
        mExpListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        mExpListView.setBackgroundColor(Color.WHITE);

        prepareListData();

        mListAdapter = new ExpandableListAdapter(getContext(), mListDataHeader, mListDataChild);

        //setting list adapter
        mExpListView.setAdapter(mListAdapter);


        final Activity mainTab = getActivity();
        database = new ExpensesDataSource(getContext());

        // Animated Floating Action Buttons
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (delOpen || combOpen) {//Cose button is clicked

                    mListAdapter.deselectAll();
                    mSelectionMode = false;
                    closeDelete();
                    closeCombine();
                    closeFab();

                    updateExpListView();
                } else {//Create new Booking button is clicked

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
                database.createChildBooking(mListAdapter.getSelectedGroupData());
                database.close();
                mListAdapter.deselectAll();
                Toast.makeText(mainTab, "Done!", Toast.LENGTH_SHORT).show();
                closeCombine();
                updateExpListView();
            }
        });

        fabDelete = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                database.open();
                database.deleteBookings(mListAdapter.getSelectedBookingIds());
                database.close();
                mListAdapter.deselectAll();
                Toast.makeText(mainTab, "Deleted all Bookings", Toast.LENGTH_SHORT).show();
                closeDelete();
                updateExpListView();
            }
        });


        test = AnimationUtils.loadAnimation(mainTab, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(mainTab, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_backward);


        //OnClickMethods for ExpandableListView

        //ExpandableListView Group click listener
        mExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {

                //get expense
                ExpenseObject expense = (ExpenseObject) mExpListView.getItemAtPosition(groupPosition);

                //When the user clicks on an Parent or on an Date divider the default behaviour should happen
                if (expense.getAccount().getIndex() == 8888)
                    return true;

                if (expense.getAccount().getIndex() == 9999)
                    return false;

                if (!mSelectionMode) {

                    Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                    openExpense.putExtra("parentExpense", expense.getIndex());
                    startActivity(openExpense);
                } else {

                    if (mListAdapter.isSelected(groupPosition)) {

                        mListAdapter.removeGroup(groupPosition);
                        view.setBackgroundColor(Color.WHITE);

                        if (mListAdapter.getSelectedCount() == 0)
                            mSelectionMode = false;
                    } else {

                        mListAdapter.selectGroup(groupPosition);
                        view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));
                    }

                    animateFabs(mListAdapter.getSelectedCount());
                }

                return true;
            }
        });


        //ExpandableListView Child click listener
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                mListAdapter.clearSelected();

                //get expense
                Object o = mExpListView.getItemAtPosition(groupPosition);
                ExpenseObject expense = (ExpenseObject) o;

                //start expenseScreen with selected expense
                Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                openExpense.putExtra("childExpense", expense.getIndex());
                startActivity(openExpense);
                return false;
            }
        });


        //ExpandableListView Long click listener for selecting multiple groups
        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //if selection mode is enabled the user can't long click elements anymore
                //instead he has to normal click them
                if (mSelectionMode)
                    return true;

                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    ExpenseObject expense = mListAdapter.getExpense(groupPosition);

                    Log.d(TAG, "onItemLongClick: " + groupPosition);

                    if (expense.getAccount().getIndex() < 8888) {

                        mListAdapter.selectGroup(groupPosition);
                        view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                        mSelectionMode = true;
                        animateFabs(mListAdapter.getSelectedCount());
                        return true;
                    }

                    return false;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    //if long click is on child element
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

        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
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

                mListDataHeader.add(dateSeparator);
                mListDataChild.put(dateSeparator, new ArrayList<ExpenseObject>());
            } else {

                mListDataHeader.add(expenses.get(i));
                mListDataChild.put(expenses.get(i), expenses.get(i).getChildren());
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

        mListAdapter.notifyDataSetChanged();
    }
}