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

    ExpensesDataSource database;
    ArrayList<ExpenseObject> mExpenses;
    List<Long> mActiveAccounts;

    FloatingActionButton fab, fabDelete, fabCombine;
    Animation test, fabClose, rotateForward, rotateBackward;
    boolean combOpen = false, delOpen = false, fabOpen = false;
    boolean mSelectionMode = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListDataHeader = new ArrayList<>();
        mActiveAccounts = new ArrayList<>();
        mListDataChild = new HashMap<>();
        mExpenses = new ArrayList<>();

        database = new ExpensesDataSource(getContext());
        setActiveAccounts(database);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (database.isOpen())
            database.close();
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

                database.createChildBooking(mListAdapter.getSelectedGroupData());
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

                database.deleteBookings(mListAdapter.getSelectedBookingIds());
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
     * Methode um die mActiveAccounts liste zu initialisieren
     *
     * @param database Datenbankverbindung
     */
    private void setActiveAccounts(ExpensesDataSource database) {

        if (!database.isOpen())
            database.open();

        SharedPreferences preferences = getContext().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        ArrayList<Account> accounts = database.getAllAccounts();

        for (Account account : accounts) {

            if (preferences.getBoolean(account.getAccountName().toLowerCase(), false))
                mActiveAccounts.add(account.getIndex());
        }
    }

    /**
     * Methode um die ExpandableListView datenitem vorzubereiten.
     * Beim vorbereiten wird die mActiveAccounts liste mit einbezogen,
     * ist das Konto einer Buchung nicht in der aktiven Kontoliste wird die Buchung auch nicht angezeigt.
     * <p>
     * <p>
     * todo funktion so umschreiben dass sie die Liste mit Buchungen speichert anstatt sie jeder mal erneut aus der Datenbank abzufragen
     * todo mExpenses liste soll bei änderungen (Buchugen werden zusammengefügt; Buchungen werden gelöscht) entsprechend angepasst werden
     * todo wenn es unter einem datum keine Buchungen mehr geben sollte muss der DatumsPlatzhalter ebenfalls entfernt werden
     * todo wenn ein Konto abgewählt wird und bei einer parentBuchung ein oder mehrere (aber nicht alle) Buchungen nicht mehr angezeigt werden,
     *      muss auch der angezeigte Preis der parentBuchung angepasst werden
     */
    private void prepareListData() {

        if (mListDataHeader.size() > 0)
            mListDataHeader.clear();

        if (!mListDataChild.isEmpty())
            mListDataChild.clear();

        if (mExpenses.size() == 0) {//wenn die Liste noch nicht erstellt wurde

            if (!database.isOpen())
                database.open();

            Calendar cal = Calendar.getInstance();
            mExpenses = database.getBookings(cal.get(Calendar.YEAR) + "-01-01 00:00:00", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(cal.getTime()));
        }


        String separatorDate = "";

        for (int i = 0; i < mExpenses.size(); ) {

            //wenn das Datum der neuen Buchung ungleich das der alten Buchung ist muss ein DatumsSeperator eingefügt werden
            //wird ein DatumsSeperator eingefügt wird der counter nicht um eins erhöht
            if (!mExpenses.get(i).getDate().equals(separatorDate)) {

                separatorDate = mExpenses.get(i).getDate();

                Account account = new Account(8888, "", 0, Currency.createDummyCurrency());

                ExpenseObject dateSeparator = new ExpenseObject(-1, "", 0, mExpenses.get(i).getDateTime(), true, Category.createDummyCategory(), null, account, null);

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
    public void changeVisibleAccounts(long accountId, boolean isChecked) {

        //wenn das Konto bereits dem gewünschten stand entspricht
        if (mActiveAccounts.contains(accountId) == isChecked)
            return;

        if (mActiveAccounts.contains(accountId) && !isChecked)
            mActiveAccounts.remove(accountId);
        else
            mActiveAccounts.add(accountId);

        updateExpListView();
    }

    /**
     * animating the FloatingActionButtons
     * todo die ganzen animations methoden noch einmal neu schreiben da ich mit den aktuellen nicht zufrieden bin
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

        prepareListData();

        //den adapter mit den neuen Daten versorgen
        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        //den Adapter mit den neuen Daten der ExpandableListView zuordnen
        mExpListView.setAdapter(mListAdapter);

        //dem Adapter bescheid geben dass neue Daten zur verfügung stehen
        mListAdapter.notifyDataSetChanged();
    }
}