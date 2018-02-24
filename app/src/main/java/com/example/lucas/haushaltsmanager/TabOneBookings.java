package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    ExpensesDataSource mDatabase;
    ArrayList<ExpenseObject> mExpenses;
    List<Long> mActiveAccounts;

    FloatingActionButton fabMainAction, fabDelete, fabCombine;
    Animation openFabAnim, closeFabAnim, rotateForwardAnim, rotateBackwardAnim;
    boolean combOpen = false, delOpen = false, fabOpen = false;
    boolean mSelectionMode = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListDataHeader = new ArrayList<>();
        mActiveAccounts = new ArrayList<>();
        mListDataChild = new HashMap<>();
        mExpenses = new ArrayList<>();

        mDatabase = new ExpensesDataSource(getContext());
        setActiveAccounts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mDatabase.isOpen())
            mDatabase.close();
    }

    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne indicators zu machen
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {

        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        //get ListView
        mExpListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        mExpListView.setBackgroundColor(Color.WHITE);

        updateExpListView();
        //prepareListDataOld();

        final Activity mainTab = getActivity();

        // Animated Floating Action Buttons
        fabMainAction = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabMainAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (delOpen || combOpen) {

                    mListAdapter.deselectAll();
                    mSelectionMode = false;
                    closeDelete();
                    closeCombine();
                    animateIconClose();

                    updateExpListView();
                } else {

                    Intent createNewBookingIntent = new Intent(mainTab, ExpenseScreen.class);
                    mainTab.startActivity(createNewBookingIntent);
                }
            }
        });

        fabCombine = (FloatingActionButton) rootView.findViewById(R.id.fab_combine);
        fabCombine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mListAdapter.getSelectedCount() > 1){


                    //todo bevor die Buchungen zusammengefügt werden sollte ein alert dialog den user nach einem namen für die KombiBuchung fragen
                    ExpenseObject parentBooking = mDatabase.createChildBooking(mListAdapter.getSelectedGroupData());
                    mExpenses.removeAll(mListAdapter.getSelectedGroupData());
                    mExpenses.add(0, parentBooking);
                    mListAdapter.deselectAll();
                    updateExpListView();
                    animateFabs(mListAdapter.getSelectedCount());
                } else {

                    long parentExpenseId = mListAdapter.getSelectedBookingIds()[0];
                    Intent createChildToBookingIntent = new Intent(mainTab, ExpenseScreen.class);
                    createChildToBookingIntent.putExtra("parentIndex", parentExpenseId);

                    mListAdapter.deselectAll();
                    mainTab.startActivity(createChildToBookingIntent);
                }

                //todo snackbar einfügen, die es ermöglicht die aktion wieder rückgängig zu machen
            }
        });

        fabDelete = (FloatingActionButton) rootView.findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDatabase.deleteBookings(mListAdapter.getSelectedBookingIds());
                mExpenses.removeAll(mListAdapter.getSelectedGroupData());
                mListAdapter.deselectAll();

                updateExpListView();
                animateFabs(mListAdapter.getSelectedCount());

                //todo snackbar einfügen die es ermöglicht die aktion wieder rückgängig zu machen
            }
        });


        openFabAnim = AnimationUtils.loadAnimation(mainTab, R.anim.fab_open);
        closeFabAnim = AnimationUtils.loadAnimation(mainTab, R.anim.fab_close);

        rotateForwardAnim = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_forward);
        rotateBackwardAnim = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_backward);


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

                    Intent updateParentExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                    updateParentExpenseIntent.putExtra("parentExpense", expense.getIndex());
                    startActivity(updateParentExpenseIntent);
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
                Intent updateChildExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                updateChildExpenseIntent.putExtra("childExpense", expense.getIndex());
                startActivity(updateChildExpenseIntent);
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
     * Methode um die mActiveAccounts liste zu initialisieren
     */
    private void setActiveAccounts() {

        Log.d(TAG, "setActiveAccounts: erneuere aktiven KontenListe");

        if (!mDatabase.isOpen())
            mDatabase.open();

        if (!mActiveAccounts.isEmpty())
            mActiveAccounts.clear();

        SharedPreferences preferences = getContext().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        for (Account account : mDatabase.getAllAccounts()) {

            if (preferences.getBoolean(account.getAccountName().toLowerCase(), false))
                mActiveAccounts.add(account.getIndex());
        }
    }

    /**
     * Methode um die ExpandableListView items vorzubereiten.
     * Beim vorbereiten wird die mActiveAccounts liste mit einbezogen,
     * ist das Konto einer Buchung nicht in der aktiven Kontoliste wird die Buchung auch nicht angezeigt.
     * <p>
     * todo wenn ein Konto abgewählt wird und bei einer parentBuchung ein oder mehrere (aber nicht alle) Buchungen nicht mehr angezeigt werden,
     * muss auch der angezeigte Preis der parentBuchung angepasst werden
     */
    //jedes mal wenn ich von tab 3 auf den ersten tab wechsle wird die funktion prepareListData ausgeführt
    private void prepareListData() {

        Log.d(TAG, "prepareListData: erstelle neue Listen daten");

        if (mListDataHeader.size() > 0)
            mListDataHeader.clear();

        if (!mListDataChild.isEmpty())
            mListDataChild.clear();

        if (mExpenses.isEmpty()) {//wenn die Liste noch nicht erstellt wurde

            if (!mDatabase.isOpen())
                mDatabase.open();

            Calendar cal = Calendar.getInstance();
            mExpenses = mDatabase.getBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));
        }


        String separatorDate = "";

        for (int i = 0; i < mExpenses.size(); ) {

            //wenn das Datum der neuen Buchung ungleich das der alten Buchung ist muss ein DatumsSeperator eingefügt werden
            //wird ein DatumsSeperator eingefügt wird der counter nicht um eins erhöht
            if (!mExpenses.get(i).getDate().equals(separatorDate)) {

                separatorDate = mExpenses.get(i).getDate();

                Account account = new Account(8888, "", 0, Currency.createDummyCurrency(getContext()));

                ExpenseObject dateSeparator = new ExpenseObject(-1, "", 0, mExpenses.get(i).getDateTime(), true, Category.createDummyCategory(getContext()), null, account, null);

                mListDataHeader.add(dateSeparator);
                mListDataChild.put(dateSeparator, new ArrayList<ExpenseObject>());
            } else {

                ExpenseObject expense = mExpenses.get(i);

                if (expense.getAccount().getIndex() == 9999) {

                    ArrayList<ExpenseObject> allowedBookings = new ArrayList<>();

                    for (ExpenseObject childExpense : expense.getChildren()) {

                        if (mActiveAccounts.contains(childExpense.getAccount().getIndex()))
                            allowedBookings.add(childExpense);
                    }

                    //wenn kein Kind erlaubt ist muss der Parent nicht angezeigt werden
                    if (allowedBookings.size() > 0) {

                        mListDataHeader.add(expense);
                        mListDataChild.put(expense, allowedBookings);
                    }
                }

                //wenn expense keine kinder hat
                if (expense.getAccount().getIndex() == 8888 || mActiveAccounts.contains(expense.getAccount().getIndex())) {

                    mListDataHeader.add(expense);
                    mListDataChild.put(expense, expense.getChildren());//sollte leer sein
                }
                i++;
            }
        }
    }

    /**
     * Methode um die ein Konto in der aktiven Kontoliste zu aktivieren oder deaktivieren
     * !Nachdem Änderungen an der aktiven Kontoliste gemacht wurden wird die ExpandableListView neu instanziiert
     *
     * @param accountId AccountId des zu ändernden Kontos
     * @param isChecked status des Kontos
     */
    public void refreshListOnAccountSelected(long accountId, boolean isChecked) {

        if (mActiveAccounts.contains(accountId) == isChecked)
            return;

        if (mActiveAccounts.contains(accountId) && !isChecked)
            mActiveAccounts.remove(accountId);
        else
            mActiveAccounts.add(accountId);

        updateExpListView();
    }

    /**
     * Methode um die ExpandableListView nach einer Änderung neu anzuzeigen.
     */
    public void updateExpListView() {

        prepareListData();

        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    /**
     * animating the FloatingActionButtons
     * todo die ganzen animations methoden noch einmal neu schreiben da ich mit den aktuellen nicht zufrieden bin
     *
     * @param selectedCount number of selected entries
     */
    private void animateFabs(int selectedCount) {

        switch (selectedCount) {

            case 0:// beide buttons müssen nicht funktional und nicht sichtbar sein
                closeCombine();
                closeDelete();
                animateIconClose();
                break;
            case 1:// beide buttons müssen sichtbar sein und auf dem combineButton muss das addChild icon zu sehen sein
                fabCombine.setImageResource(R.drawable.ic_add_child_white);
                openDelete();
                openCombine();
                animateIconOpen();
                break;
            default:// beide buttons müssen sichtbar und funktional sein und auf dem combineButton muss das combineBookings icon sichtbar sein
                fabCombine.setImageResource(R.drawable.ic_combine_white);
                openCombine();
                openDelete();
                animateIconOpen();
                break;
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um 45°.
     */
    public void animateIconOpen() {

        if (!fabOpen) {

            fabMainAction.startAnimation(rotateForwardAnim);
            fabOpen = true;
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um -45°.
     */
    public void animateIconClose() {

        if (fabOpen) {

            fabMainAction.startAnimation(rotateBackwardAnim);
            fabOpen = false;
        }
    }

    /**
     * Methode die den LöschFab sichtbar und anklickbar macht.
     */
    public void openDelete() {

        if (!delOpen) {

            fabDelete.startAnimation(openFabAnim);
            fabDelete.setClickable(true);

            delOpen = true;
        }
    }

    /**
     * Methode die den LöschFab unsichtbar und nicht mehr anklickbar macht.
     */
    public void closeDelete() {

        if (delOpen) {

            fabDelete.startAnimation(closeFabAnim);
            fabDelete.setClickable(false);

            delOpen = false;
        }
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void openCombine() {

        if (!combOpen) {

            fabCombine.startAnimation(openFabAnim);
            fabCombine.setClickable(true);

            combOpen = true;
        }
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void closeCombine() {

        if (combOpen) {

            fabCombine.startAnimation(closeFabAnim);
            fabCombine.setClickable(false);

            combOpen = false;
        }
    }
}