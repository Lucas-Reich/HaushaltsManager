package com.example.lucas.haushaltsmanager.Activities.MainTab;

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

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreenActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TabOneBookings extends Fragment {
    private String TAG = TabOneBookings.class.getSimpleName();

    ExpandableListAdapter mListAdapter;
    ExpandableListView mExpListView;
    List<ExpenseObject> mListDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> mListDataChild;

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
        mDatabase.open();
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

                    if (mDatabase.getAllAccounts().size() != 0) {

                        Intent createNewBookingIntent = new Intent(mainTab, ExpenseScreenActivity.class);
                        createNewBookingIntent.putExtra("mode", "createBooking");
                        mainTab.startActivity(createNewBookingIntent);
                    } else {

                        //todo zeige dem user wie er ein neues Konto anlegen kann
                        Toast.makeText(mainTab, getResources().getString(R.string.no_account), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fabCombine = (FloatingActionButton) rootView.findViewById(R.id.fab_combine);
        fabCombine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mListAdapter.getSelectedCount() > 1) {

                    //todo bevor die Buchungen zusammengefügt werden sollte ein alert dialog den user nach einem namen für die KombiBuchung fragen
                    Bundle bundle = new Bundle();
                    bundle.putString("title", getResources().getString(R.string.input_title));

                    BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
                    textInputDialog.setArguments(bundle);
                    textInputDialog.show(getActivity().getFragmentManager(), "tab_one_combine_bookings");

                } else {

                    //wenn zu einer buchung eine Kindbuchung hinzugefügt werden soll, dann muss die id des Parents mit übergeben werden
                    long parentExpenseId = mListAdapter.getSelectedBookingIds()[0];
                    Intent createChildToBookingIntent = new Intent(mainTab, ExpenseScreenActivity.class);
                    createChildToBookingIntent.putExtra("mode", "addChild");
                    createChildToBookingIntent.putExtra("parentBooking", parentExpenseId);

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

                //todo wenn man auf den date divider klickt sieht man immer noch eine klickt animation
                if (expense.getExpenseType() == ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER)
                    return true;

                //if user clicks on parent the default behaviour should happen
                if (expense.isParent())
                    return false;

                if (!mSelectionMode) {

                    Intent updateParentExpenseIntent = new Intent(getContext(), ExpenseScreenActivity.class);
                    updateParentExpenseIntent.putExtra("mode", "updateParent");
                    updateParentExpenseIntent.putExtra("updateParentExpense", expense);
                    //updateParentExpenseIntent.putExtra("parentExpense", expense.getIndex());
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
                Intent updateChildExpenseIntent = new Intent(getContext(), ExpenseScreenActivity.class);
                updateChildExpenseIntent.putExtra("mode", "updateChild");
                updateChildExpenseIntent.putExtra("updateChildExpense", expense);
                //updateChildExpenseIntent.putExtra("childExpense", expense.getIndex());
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
                    //todo wenn auf eine Zusammengefügte buchung geklickt wurde sollen die Fab optionen 'lösche' und 'füge kind buchung hinzu' erscheinen

                    if (expense.isValidExpense()) {

                        mListAdapter.selectGroup(groupPosition);
                        view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                        mSelectionMode = true;
                        animateFabs(mListAdapter.getSelectedCount());
                        return true;
                    }

                    return false;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    //TODO öffne den fab und zeige die optionen 'löschen' und 'extrahiere als normale buchung' an
                    Toast.makeText(getContext(), "CHILD", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });
        return rootView;
    }

    /**
     * Methode um die mActiveAccounts liste zu initialisieren.
     * Dabei werden die Indizes der akitven Konten in der mActiveAccounts liste gespeichert
     */
    private void setActiveAccounts() {
        Log.d(TAG, "setActiveAccounts: erneuere aktiven Kontenliste");
        mActiveAccounts.clear();

        SharedPreferences preferences = getContext().getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE);

        for (Account account : mDatabase.getAllAccounts()) {

            if (preferences.getBoolean(account.getName(), false))
                mActiveAccounts.add(account.getIndex());
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

        prepareDataSources();

        mListAdapter = new ExpandableListAdapterCreator(mExpenses, mActiveAccounts, getContext()).getExpandableListAdapter();

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um die Ausgabenliste zu initialiesieren, wenn dies noch nicht geschehen ist
     */
    private void prepareDataSources() {

        if (mExpenses.isEmpty()) {

            Log.d(TAG, "prepareDataSources: Initialisiere Buchungsliste.");
            mExpenses = mDatabase.getBookings(getFirstOfMonth().getTimeInMillis(), getLastOfMonth().getTimeInMillis());
        }
    }

    /**
     * Methode um eine Kalendar Objekt zu erstellen, welches dem ersten Tag des Monats entspricht.
     *
     * @return Erster Tag des Monats
     */
    private Calendar getFirstOfMonth() {

        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstOfMonth.set(Calendar.MINUTE, 0);
        firstOfMonth.set(Calendar.SECOND, 1);
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        return firstOfMonth;
    }

    /**
     * Methode um ein Kalendar objekt zu erstellen, welches dem letzten Tag des Monats entspricht.
     *
     * @return Letzter Tag des Monats
     */
    private Calendar getLastOfMonth() {

        int lastDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar lastOfMonth = Calendar.getInstance();
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastDayMonth);

        return lastOfMonth;
    }

    /**
     * animating the FloatingActionButtons
     * todo die ganzen animations methoden noch einmal neu schreiben da ich mit den aktuellen nicht zufrieden bin
     * eventuell eine neue FAB view erstellen, welche die funktionalitäten beinhaltet
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

    /**
     * Methode um einer kombinierten Buchung einen Titel zu geben
     *
     * @param title Titel der zusammengefügten Buchung
     */
    public void onCombinedTitleSelected(String title) {

        ExpenseObject parentBooking = mDatabase.combineChildBookings(mListAdapter.getSelectedGroupData());
        parentBooking.setTitle(title);
        mDatabase.updateBooking(parentBooking);

        mExpenses.removeAll(mListAdapter.getSelectedGroupData());
        mExpenses.add(0, parentBooking);
        mListAdapter.deselectAll();
        updateExpListView();
        animateFabs(mListAdapter.getSelectedCount());
    }
}