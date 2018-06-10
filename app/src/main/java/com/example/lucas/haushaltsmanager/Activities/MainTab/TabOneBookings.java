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
    private static final String TAG = TabOneBookings.class.getSimpleName();

    ExpandableListAdapter mListAdapter;
    ExpandableListView mExpListView;
    List<ExpenseObject> mListDataHeader;
    HashMap<ExpenseObject, List<ExpenseObject>> mListDataChild;

    ExpensesDataSource mDatabase;
    ArrayList<ExpenseObject> mExpenses;
    List<Long> mActiveAccounts;

    FloatingActionButton fabBig, fabSmallTop, fabSmallLeft;
    Animation openFabAnim, closeFabAnim, rotateForwardAnim, rotateBackwardAnim;


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

        mExpListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
        mExpListView.setBackgroundColor(Color.WHITE);
        setOnGroupClickListener();
        setOnChildClickListener();
        setOnItemLongClickListener();

        updateExpListView();

        final Activity mainTab = getActivity();


        fabBig = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabBig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (hasUserSelectedItems()) {

                    resetActivityViewState();
                } else {

                    if (mDatabase.getAllAccounts().size() != 0) {//todo elegantere Möglichkeit finden den user zu zwingen ein Konto zu erstellen, bevor er eine Buchung erstellt

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

        fabSmallLeft = (FloatingActionButton) rootView.findViewById(R.id.fab_small_left);
        fabSmallLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (combineBookingsMode()) {

                    Bundle bundle = new Bundle();
                    bundle.putString("title", getResources().getString(R.string.input_title));

                    BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
                    textInputDialog.setArguments(bundle);
                    if (mListAdapter.getSelectedGroupCount() == mListAdapter.getSelectedItemsCount())
                        textInputDialog.show(getActivity().getFragmentManager(), "tab_one_combine_parents");
                    else if (mListAdapter.getSelectedGroupCount() == mListAdapter.getSelectedItemsCount())
                        textInputDialog.show(getActivity().getFragmentManager(), "tab_one_combine_groups");
                    else if (mListAdapter.getSelectedGroupCount() >= 1 || mListAdapter.getSelectedParentCount() >= 1)//todo der titel sollte nich neu erstellt werden
                        textInputDialog.show(getActivity().getFragmentManager(), "tab_one_combine_parents_groups");
                }

                if (addChildMode()) {

                    ExpenseObject parentExpense = mListAdapter.getSelectedGroupData().get(0);

                    Intent createChildToBookingIntent = new Intent(mainTab, ExpenseScreenActivity.class);
                    createChildToBookingIntent.putExtra("mode", "addChild");
                    createChildToBookingIntent.putExtra("parentBooking", parentExpense);

                    resetActivityViewState();
                    mainTab.startActivity(createChildToBookingIntent);
                }

                if (extractChildMode()) {

                    mDatabase.convertChildrenToBooking(mListAdapter.getSelectedChildData());
                    resetActivityViewState();
                    //todo mExpenses neu laden, da convertChildrenToBookings die angepassten Buchungen nicht zurück gibt
                }

                //todo snackbar einfügen, die es ermöglicht die aktion wieder rückgängig zu machen
            }
        });

        fabSmallTop = (FloatingActionButton) rootView.findViewById(R.id.fab_small_top);
        fabSmallTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mDatabase.deleteChildBookings(mListAdapter.getSelectedChildData());
                mDatabase.deleteBookings(mListAdapter.getSelectedGroupData());

                resetActivityViewState();
                //todo snackbar einfügen die es ermöglicht die aktion wieder rückgängig zu machen
            }
        });


        openFabAnim = AnimationUtils.loadAnimation(mainTab, R.anim.fab_open);
        closeFabAnim = AnimationUtils.loadAnimation(mainTab, R.anim.fab_close);

        rotateForwardAnim = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_forward);
        rotateBackwardAnim = AnimationUtils.loadAnimation(mainTab, R.anim.rotate_backward);

        return rootView;
    }

    /**
     * Methdoe um einen ChildClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnChildClickListener() {
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                ExpenseObject childExpense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                if (!hasUserSelectedItems()) {

                    Intent updateChildExpenseIntent = new Intent(getContext(), ExpenseScreenActivity.class);
                    updateChildExpenseIntent.putExtra("mode", "updateChild");
                    updateChildExpenseIntent.putExtra("updateChildExpense", childExpense);

                    resetActivityViewState();
                    startActivity(updateChildExpenseIntent);
                    return true;
                }

                ExpenseObject parentExpense = (ExpenseObject) mListAdapter.getGroup(groupPosition);
                if (!mListAdapter.isGroupSelected(parentExpense)) {
                    if (mListAdapter.isChildSelected(childExpense)) {

                        mListAdapter.removeChildFromList(childExpense);
                        view.setBackgroundColor(Color.WHITE);

                        if (mListAdapter.getSelectedItemsCount() == 0)
                            enableLongClick();
                    } else {

                        mListAdapter.selectChild(childExpense);
                        view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));
                    }
                } else {

                    Toast.makeText(getActivity(), R.string.error_parent_already_highlighted, Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    /**
     * Methode um einen GroupClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnGroupClickListener() {
        mExpListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
                ExpenseObject groupExpense = (ExpenseObject) mListAdapter.getGroup(groupPosition);

                if (mListAdapter.getSelectedItemsCount() == 0 && groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE) {

                    //falls keine Buchung markiert ist soll die Buchung im ExpenseScreen aufgerufen werden
                    Intent updateParentExpenseIntent = new Intent(getContext(), ExpenseScreenActivity.class);
                    updateParentExpenseIntent.putExtra("mode", "updateParent");
                    updateParentExpenseIntent.putExtra("updateParentExpense", groupExpense);

                    resetActivityViewState();
                    startActivity(updateParentExpenseIntent);
                    return true;
                }

                switch (groupExpense.getExpenseType()) {
                    case TRANSFER_EXPENSE:
                        //ignoriere transferBuchungen
                        return true;
                    case DATE_PLACEHOLDER:
                        //ignoriere Datumstrenner
                        return true;
                    case PARENT_EXPENSE:
                        if (!mListAdapter.isGroupSelected(groupExpense)) {
                            if (hasUserSelectedItems())
                                mListAdapter.selectGroup(groupExpense);
                            animateFabs2(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                        }
                        return false;
                    case NORMAL_EXPENSE:
                    case CHILD_EXPENSE:

                        if (mListAdapter.isGroupSelected(groupExpense)) {

                            mListAdapter.removeGroupFromList(groupExpense);
                            view.setBackgroundColor(Color.WHITE);

                            if (mListAdapter.getSelectedItemsCount() == 0)
                                enableLongClick();
                        } else {

                            mListAdapter.selectGroup(groupExpense);
                            view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));
                        }
                        animateFabs2(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    /**
     * Methode um herauszufinden, ob wir und im extractChildrenMode befinden.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean extractChildMode() {

        boolean areChildrenSelected = mListAdapter.getSelectedChildCount() > 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() == 0;

        return areChildrenSelected;
    }

    /**
     * Methode um herauszufinden, ob wir uns im combineBookingsMode sind.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean combineBookingsMode() {

        boolean areGroupsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() > 1;

        boolean areParentsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() > 1
                && mListAdapter.getSelectedGroupCount() == 0;

        boolean areParentAndGroupsSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() >= 1
                && mListAdapter.getSelectedGroupCount() >= 0;

        return areGroupsSelected || areParentsSelected || areParentAndGroupsSelected;
    }

    /**
     * Methode um herauszufinden, ob wir momentan im addChildMode sind.
     *
     * @return True wenn dem so ist, False wenn nicht
     */
    private boolean addChildMode() {

        boolean isGroupSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() == 1;

        boolean isParentSelected = mListAdapter.getSelectedChildCount() == 0
                && mListAdapter.getSelectedParentCount() == 1
                && mListAdapter.getSelectedGroupCount() == 0;

        return isGroupSelected ^ isParentSelected;
    }

    /**
     * Methode um herauszufinden ob der User Elemente in der Liste ausgewählt hat oder nicht.
     *
     * @return True wenn Elemente ausgewählt sind, False wenn nicht
     */
    private boolean hasUserSelectedItems() {
        return mListAdapter.getSelectedItemsCount() != 0;
    }

    /**
     * Methode um einen LongClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnItemLongClickListener() {
        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                switch (ExpandableListView.getPackedPositionType(id)) {

                    case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
                        ExpenseObject groupExpense = (ExpenseObject) mListAdapter.getGroup(groupPosition);
                        //todo wenn auf eine Zusammengefügte buchung geklickt wurde sollen die Fab optionen 'lösche' und 'füge kind buchung hinzu' erscheinen

                        if (groupExpense.isValidExpense()) {

                            mListAdapter.selectGroup(groupExpense);
                            view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                            disableLongClick();
                            animateFabs2(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                            return true;
                        }

                        return false;
                    case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                        ExpenseObject childExpense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                        if (childExpense.isValidExpense()) {

                            mListAdapter.selectChild(childExpense);
                            view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                            disableLongClick();
                            animateFabs2(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());

                            return true;
                        }
                        return true;
                    default:

                        return false;
                }
            }
        });
    }

    /**
     * Helper Methode um den Longclick der ExpandableListView zu deaktivieren
     */
    private void disableLongClick() {
        mExpListView.setLongClickable(false);
    }

    /**
     * Helper Methode um den Longclick der ExpandableListView zu aktivieren
     */
    private void enableLongClick() {
        mExpListView.setLongClickable(true);
    }

    /**
     * Methode um die View in ihren Ausgangszustand zurückzusetzen.
     */
    private void resetActivityViewState() {

        resetExpandableListView();
        resetButtonAnimations();
    }

    /**
     * Methode umd die ExpandableListView in ihren Ausgangstzstand zurückzusetzen.
     */
    private void resetExpandableListView() {

        mListAdapter.deselectAll();
        updateExpListView();
        enableLongClick();
    }

    /**
     * Methode um die Buttonanimationen auf ihre Uhrsprungszustand zurückzusetzen
     */
    private void resetButtonAnimations() {

        closeFabSmallTop();
        closeFabSmallLeft();
        animatePlusClose();
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

            if (preferences.getBoolean(account.getTitle(), false))
                mActiveAccounts.add(account.getIndex());
        }
    }

    /**
     * Methode um einer kombinierten Buchung einen Titel zu geben
     *
     * @param title Titel der zusammengefügten Buchung
     */
    public void onCombinedTitleSelected(String title, String tag) {

        ExpenseObject parentBooking;
        switch (tag) {
            case "tab_one_combine_parents":
                parentBooking = mDatabase.combineAsChildBookings(mListAdapter.getSelectedGroupData());
                mExpenses.removeAll(mListAdapter.getSelectedGroupData());
                break;
            case "tab_one_combine_groups":
                parentBooking = mDatabase.combineAsChildBookings(mListAdapter.getSelectedGroupData());
                mExpenses.removeAll(mListAdapter.getSelectedGroupData());
                break;
            case "tab_one_combine_parents_groups":
                parentBooking = mDatabase.combineParentBookings(mListAdapter.getSelectedGroupData());
                mExpenses.removeAll(mListAdapter.getSelectedGroupData());
                //removeChildrenFromListView(mListAdapter.getSelectedChildData());
                break;
            default:
                throw new UnsupportedOperationException("Kombimöglichkeit " + tag + " wird nicht unterstützt!");
        }

        parentBooking.setTitle(title);
        mDatabase.updateBooking(parentBooking);
        mExpenses.add(0, parentBooking);

        resetActivityViewState();
    }

    /**
     * Methode, welche die angegebenen Kinder aus der ListView löscht.
     *
     * @param childrenToRemove Zu löschende Kinder
     */
    public void removeChildrenFromListView(ArrayList<ExpenseObject> childrenToRemove) {

        for (ExpenseObject child : childrenToRemove) {
            for (ExpenseObject expense : mExpenses) {
                if (expense.getChildren().contains(child)) {
                    expense.getChildren().remove(child);
                    break;
                }
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
                resetButtonAnimations();
                break;
            case 1:// beide buttons müssen sichtbar sein und auf dem combineButton muss das addChild icon zu sehen sein
                fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);
                openFabSmallTop();
                openFabSmallLeft();
                animatePlusOpen();
                break;
            default:// beide buttons müssen sichtbar und funktional sein und auf dem combineButton muss das combineBookings icon sichtbar sein
                fabSmallLeft.setImageResource(R.drawable.ic_combine_white);
                openFabSmallLeft();
                openFabSmallTop();
                animatePlusOpen();
                break;
        }
    }

    /**
     * Methode die für die Animationen der FloatingActionButtons zuständig ist.
     *
     * @param selectedGroups   Anzahl der ausgewählten Gruppen
     * @param selectedChildren Anzahl der ausgewählten Kinder
     */
    private void animateFabs2(int selectedGroups, int selectedChildren, int selectedParents) {

        //wenn keine buchung ausgewählt ist sollen die Buttons in den Normalzustand zurückkehren
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 0) {
            resetButtonAnimations();
        }

        //wenn eine Group ausgewählt ist sollen die Buttons add child und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 1) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);

            animatePlusOpen();
        }

        //wenn zwei Groups ausgewählt sind sollen die Buttons Combine und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups > 1) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_combine_white);

            animatePlusOpen();
        }

        //wenn eine Group und ein Parent ausgewählt sind sollen der Button AddToParent angezezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups > 0) {

            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_merge_bookings_white);
        }

        //wenn eine oder mehrere ChildBuchung/en ausgewählt ist/sind sollen die Buttons extract und delete angezeigt werden
        if (selectedChildren > 0 && selectedParents == 0 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_extract_child_white);

            animatePlusOpen();
        }

        //wenn eine ParentBuchung ausgewählt ist sollen die Buttons add Child und delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);

            animatePlusOpen();
        }

        //wenn eine oder mehrere ParentBuchungen ausgewählt sind sollen die Buttons combine und delete angezeigt werden
        if (selectedChildren == 0 && selectedParents > 1 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_merge_bookings_white);

            animatePlusOpen();
        }
    }

    /**
     * Methode um einen FloatingActionButton anzuzeigen.
     *
     * @param fab FAB
     */
    public void showFab(FloatingActionButton fab) {

        if (fab.getVisibility() != View.VISIBLE) {

            fab.setVisibility(View.VISIBLE);
            fab.startAnimation(openFabAnim);
            fab.setClickable(true);
        }
    }

    /**
     * Methode um einen FloatingActinButton zu verstecken.
     *
     * @param fab FAB
     */
    public void closeFab(FloatingActionButton fab) {

        if (fab.getVisibility() != View.GONE) {

            fab.setVisibility(View.GONE);
            fab.startAnimation(closeFabAnim);
            fab.setClickable(false);
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um 45°.
     */
    public void animatePlusOpen() {
        //todo das plus dreht sich jedes mal wenn die funktion aufgerufen wird, auch wenn der button bereits offen ist
        fabBig.startAnimation(rotateForwardAnim);
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um -45°.
     */
    public void animatePlusClose() {
        //todo das plus dreht sich jedes mal wenn die funktion aufgerufen wird, auch wenn der button bereits offen ist
        fabBig.startAnimation(rotateBackwardAnim);
    }

    /**
     * Methode die den LöschFab sichtbar und anklickbar macht.
     */
    public void openFabSmallTop() {
        showFab(fabSmallTop);
    }

    /**
     * Methode die den LöschFab unsichtbar und nicht mehr anklickbar macht.
     */
    public void closeFabSmallTop() {
        closeFab(fabSmallTop);
    }

    /**
     * Methode die den KombinierFab sichtbar und anklickbar macht.
     */
    public void openFabSmallLeft() {
        showFab(fabSmallLeft);
    }

    /**
     * Methode die den KombinierFab unsichtbar und nicht mehr anklickbar macht.
     */
    public void closeFabSmallLeft() {
        closeFab(fabSmallLeft);
    }
}