package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Context;
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

        database = new ExpensesDataSource(getContext());

        prepareListData();

        mListAdapter = new ExpandableListAdapter(getContext(), mListDataHeader, mListDataChild);

        //setting list adapter
        mExpListView.setAdapter(mListAdapter);


        final Activity mainTab = getActivity();

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

                updateExpListView();
                animateFabs(mListAdapter.getSelectedCount());
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

                updateExpListView();
                animateFabs(mListAdapter.getSelectedCount());
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
                ExpenseObject expense = (ExpenseObject) mListAdapter.getGroup(groupPosition);

                //if the user clicks on date divider nothing should happen
                if (expense.getAccount().getIndex() == 8888)
                    return true;

                //if user clicks on parent the defualt behaviour should happen
                if (expense.getAccount().getIndex() == 9999)
                    return false;

                if (!mSelectionMode) {

                    Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                    openExpense.putExtra("parentExpense", expense.getIndex());
                    startActivity(openExpense);
                } else {

                    if (mListAdapter.isSelected(groupPosition)) {

                        mListAdapter.removeGroupFromList(groupPosition);
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

                if (mSelectionMode)
                    return true;

                mListAdapter.clearSelected();

                //get expense
                ExpenseObject expense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                Log.d(TAG, "onChildClick: " + expense.getTitle() + " " + expense.getIndex());

                //start expenseScreen with selected expense
                Intent openExpense = new Intent(getContext(), ExpenseScreen.class);
                openExpense.putExtra("childExpense", expense.getIndex());
                startActivity(openExpense);
                return true;
            }
        });


        //ExpandableListView Long click listener for selecting multiple groups
        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //if selection mode is enabled do not make long clicks anymore
                if (mSelectionMode)
                    return true;

                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    ExpenseObject expense = mListAdapter.getExpense(groupPosition);

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
     * Methode um die Ausgaben für die ExpandableListView vorzubereiten
     */
    private void prepareListData() {

        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
        ArrayList<ExpenseObject> expenses;

        database = new ExpensesDataSource(getContext());

        database.open();
        Calendar cal = Calendar.getInstance();
        //todo wechsle zu getBookingsForActiveAccounts wenn die funktion richtig funktioniert
        expenses = database.getBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));

        String separatorDate = "";

        for (int i = 0; i < expenses.size(); ) {

            if (!expenses.get(i).getDate().equals(separatorDate)) {

                separatorDate = expenses.get(i).getDate();

                Category category = new Category(null, "alterVisibleAccounts", "#000000", false);
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

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um 45°.
     */
    public void openFab() {

        if (!fabOpen) {

            fab.startAnimation(rotateForward);
            fabOpen = true;
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um -45°.
     */
    public void closeFab() {

        if (fabOpen) {

            fab.startAnimation(rotateBackward);
            fabOpen = false;
        }
    }

    /**
     * Methode die den LöschFab sichtbar und anklickbar macht.
     */
    public void openDelete() {

        if (!delOpen) {

            fabDelete.startAnimation(test);
            fabDelete.setClickable(true);

            delOpen = true;
        }
    }

    /**
     * Methode die den LöschFab unsichtbar und nicht mehr anklickbar macht.
     */
    public void closeDelete() {

        if (delOpen) {

            fabDelete.startAnimation(fabClose);
            fabDelete.setClickable(false);

            delOpen = false;
        }
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void openCombine() {

        if (!combOpen) {

            fabCombine.startAnimation(test);
            fabCombine.setClickable(true);

            combOpen = true;
        }
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void closeCombine() {

        if (combOpen) {

            fabCombine.startAnimation(fabClose);
            fabCombine.setClickable(false);

            combOpen = false;
        }
    }

    /**
     * Methode um die ExpandableListView nach einer Änderung neu anzuzeigen.
     * <p>
     * todo bei vielen Buchungen kann diese Operation eventuell sehr resourcen intensiv sein
     */
    public void updateExpListView() {

        Log.d(TAG, "updateExpListView: " + isAdded());

        //änderungen aus der Datenbank holen
        prepareListData();

        //den adapter mit den neuen Daten versorgen
        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        //den Adapter mit den neuen Daten der ExpandableListView zuordnen
        mExpListView.setAdapter(mListAdapter);

        //dem Adapter bescheid geben dass neue Daten zur verfügung stehen
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ich bin gerade in die pause gegangen und deshalb kann man mich momentan nicht ansprechen");
        super.onPause();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ich wurde gerade von meinem parent abgekoppelt und deshalb bekommt man bei getContext() jetzt auch null");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: fikluhgwiklhlsuitbgnhluiwtrhglhstronjslrothnoistrhnlostrhnstrujhsntruiogl");
        super.onDestroy();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        Log.d(TAG, "onAttachFragment: ikujhrgbhkwuriehgwueirghlwerguhlwteioguhbnluowtrghleuotrhbgiuewtbhgluoteghqoeurhbg");
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: reajikhbglrehkiuqehrgiuaehrbguiahlrioehgsoirenhotirsnvghwlszhnvuiohwzuiwhtoiu");
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: rlieukgnhweuirghwerhgoiwet5zhglöiotrzjhoitnbvgziolsthsköt6orjnhiostrdnh");
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: ljikwrgqhbuirebgiuiwreugjhnleroghbuireahngvfhejrailtcghaoirezvnhtoui5ezhtiouw5eauio");
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: elrgikhgziuenhrgihnfurthawlsecuihr,oiuthauieo5htrzouirhblgaurjgihblgi");
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: rqlkghtbireuhkgquiehncvfuiahtlgzsvhlzoiusahöiuzhgöoisrehoöiuvrnhtzuilesrzhiulhauilhzoh5reöpazhuj5tp0azhö");
        super.onResume();
    }
}