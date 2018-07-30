package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreenActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TabOneBookings extends Fragment {
    private static final String TAG = TabOneBookings.class.getSimpleName();

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpListView;
    private FloatingActionButton fabBig, fabSmallTop, fabSmallLeft;
    private Animation openFabAnim, closeFabAnim, rotateForwardAnim, rotateBackwardAnim;
    private boolean fabBigIsAnimated = false;
    private ParentActivity mParent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
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

        updateView();

        final Activity mainTab = getActivity();

        fabBig = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabBig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (hasUserSelectedItems()) {

                    resetActivityViewState();
                } else {

                    if (AccountRepository.getAll().size() != 0) {//todo elegantere Möglichkeit finden den user zu zwingen ein Konto zu erstellen, bevor er eine Buchung erstellt

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
                    textInputDialog.setOnTextInputListener(new BasicTextInputDialog.BasicDialogCommunicator() {
                        @Override
                        public void onTextInput(String title) {

                            ExpenseObject parentBooking = ChildExpenseRepository.combineExpenses(mListAdapter.getSelectedGroupData());
                            parentBooking.setTitle(title);
                            ExpenseRepository.update(parentBooking);

                            mParent.deleteGroupBookings(mListAdapter.getSelectedGroupData());
                            mParent.addGroupBooking(parentBooking);

                            resetActivityViewState();
                        }
                    });
                    textInputDialog.setArguments(bundle);
                    textInputDialog.show(getActivity().getFragmentManager(), "");
                }

                if (addChildMode()) {

                    ExpenseObject parentExpense = mListAdapter.getSelectedGroupData().get(0);

                    Intent createChildToBookingIntent = new Intent(mainTab, ExpenseScreenActivity.class);
                    createChildToBookingIntent.putExtra("mode", "addChild");
                    createChildToBookingIntent.putExtra("parentBooking", parentExpense);

                    //todo die Änderung auch mParent mitteilen
                    resetActivityViewState();
                    mainTab.startActivity(createChildToBookingIntent);
                }

                if (extractChildMode()) {

                    for (ExpenseObject child : mListAdapter.getSelectedChildData()) {
                        ChildExpenseRepository.extractChildFromBooking(child);
                    }

                    //todo die Änderung auch mParent mitteilen
                    resetActivityViewState();
                }
            }
        });

        fabSmallTop = (FloatingActionButton) rootView.findViewById(R.id.fab_small_top);
        fabSmallTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showSnackbar(
                        mListAdapter.getSelectedGroupData(),
                        mListAdapter.getSelectedMappedChildData(),
                        R.string.revert_deletion,
                        R.string.bookings_successfully_restored
                );

                for (ExpenseObject child : mListAdapter.getSelectedChildData()) {
                    ChildExpenseRepository.delete(child);
                }
                mParent.deleteChildBookings(mListAdapter.getSelectedMappedChildData());

                for (ExpenseObject parent : mListAdapter.getSelectedGroupData()) {
                    try {
                        ExpenseRepository.delete(parent);
                    } catch (CannotDeleteExpenseException e) {
                        //todo was soll passieren wenn eine Buchung nicht gelöscht werden kann?
                    }
                }
                mParent.deleteGroupBookings(mListAdapter.getSelectedGroupData());

                resetActivityViewState();
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

                if (mListAdapter.getSelectedItemsCount() == 0 && groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE && groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER) {

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
                            animateFabs(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                        } else {
                            mListAdapter.removeGroupFromList(groupExpense);
                            animateFabs(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());

                            if (mListAdapter.getSelectedItemsCount() == 0)
                                enableLongClick();
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
                        animateFabs(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
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

        return mListAdapter.getSelectedChildCount() > 0
                && mListAdapter.getSelectedParentCount() == 0
                && mListAdapter.getSelectedGroupCount() == 0;
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
                && mListAdapter.getSelectedGroupCount() >= 1;

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

                        if (groupExpense.isValidExpense()) {

                            mListAdapter.selectGroup(groupExpense);
                            view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                            disableLongClick();
                            animateFabs(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                            return true;
                        }

                        return false;
                    case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
                        ExpenseObject childExpense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                        if (childExpense.isValidExpense()) {

                            mListAdapter.selectChild(childExpense);
                            view.setBackgroundColor(getResources().getColor(R.color.highlighted_item_color));

                            disableLongClick();
                            animateFabs(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());

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
        updateView();
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
     * Methode um die ExpandableListView nach einer Änderung neu anzuzeigen.
     */
    public void updateView() {

        mListAdapter = new ExpandableListAdapterCreator(
                mParent.getExpenses(getFirstOfMonth(), getLastOfMonth()),
                mParent.getActiveAccounts(),
                getContext()
        ).getExpandableListAdapter();

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
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
     * Methode die für die Animationen der FloatingActionButtons zuständig ist.
     *
     * @param selectedGroups   Anzahl der ausgewählten Gruppen
     * @param selectedChildren Anzahl der ausgewählten Kinder
     */
    private void animateFabs(int selectedGroups, int selectedChildren, int selectedParents) {

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

        //wenn eine oder mehrere ChildBuchung/en ausgewählt ist/sind sollen die Buttons extract und deleteAll angezeigt werden
        if (selectedChildren > 0 && selectedParents == 0 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_extract_child_white);

            animatePlusOpen();
        }

        //wenn eine ParentBuchung ausgewählt ist sollen die Buttons add Child und deleteAll angezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups == 0) {

            openFabSmallTop();
            openFabSmallLeft();
            fabSmallLeft.setImageResource(R.drawable.ic_add_child_white);

            animatePlusOpen();
        }

        //wenn eine oder mehrere ParentBuchungen ausgewählt sind sollen die Buttons combine und deleteAll angezeigt werden
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
        if (!fabBigIsAnimated) {
            fabBig.startAnimation(rotateForwardAnim);
            fabBigIsAnimated = true;
        }
    }

    /**
     * Methode die das plus auf dem Button animiert.
     * Wird diese Animation getriggert dreht sich das Pluszeichen um -45°.
     */
    public void animatePlusClose() {
        if (fabBigIsAnimated) {
            fabBig.startAnimation(rotateBackwardAnim);
            fabBigIsAnimated = false;
        }
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

    /**
     * Methode um eine Snackbar anzuzeigen.
     *
     * @param groups         Gruppen die gelöscht wurden
     * @param children       Kinder die gelöscht wurden
     * @param message        Nachricht die in der Snackbar angezeigt werden soll
     * @param successMessage Nachricht die angezeigt wird wenn alles erfolgreich bearbeitet wurde
     */
    private void showSnackbar(ArrayList<ExpenseObject> groups, HashMap<Long, ExpenseObject> children, @StringRes int message, @StringRes int successMessage) {

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.tab_one_bookings_layout);
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.revert_action, new UndoDeletionClickListener(groups, children, successMessage))
                .show();
    }

    /**
     * Klasse die einen ClickListener für gelöschte Buchungen implementiert, welcher die gelöschten GroupBuchungen und ChildBuchungen als Argumente übernimmt.
     */
    class UndoDeletionClickListener implements View.OnClickListener {

        private ArrayList<ExpenseObject> mDeletedGroups;
        private HashMap<Long, ExpenseObject> mDeletedChildren;
        private String mSuccessMessage;

        @SuppressLint("UseSparseArrays")
        UndoDeletionClickListener(ArrayList<ExpenseObject> deletedGroups, HashMap<Long, ExpenseObject> deletedChildren, @StringRes int successMessage) {

            mDeletedGroups = new ArrayList<>(deletedGroups);
            mDeletedChildren = new HashMap<>(deletedChildren);
            mSuccessMessage = getString(successMessage);
        }

        @Override
        public void onClick(View v) {

            for (ExpenseObject group : mDeletedGroups) {
                ExpenseRepository.insert(group);
            }

            for (final Map.Entry<Long, ExpenseObject> deletedChild : mDeletedChildren.entrySet()) {
                try {

                    ExpenseObject parentExpense = ExpenseRepository.get(deletedChild.getKey());
                    ChildExpenseRepository.addChildToBooking(deletedChild.getValue(), parentExpense);
                    mParent.addChildBooking(deletedChild.getKey().intValue(), deletedChild.getValue());
                } catch (ExpenseNotFoundException e) {

                    ChildExpenseRepository.combineExpenses(new ArrayList<ExpenseObject>() {{
                        deletedChild.getKey();
                    }});
                } catch (AddChildToChildException e) {

                    //todo übersetzung
                    Toast.makeText(mParent, "Du kannst keine Kindbuchung zu Kindbuchungen hinzufügen!", Toast.LENGTH_SHORT).show();
                }

            }

            // todo überprüfen ob die buchung wirklich wiederhergestellt wurde
            Toast.makeText(getContext(), mSuccessMessage, Toast.LENGTH_SHORT).show();
            updateView();
        }
    }


    /**
     * Methode um herauszufinden, ob der aktuelle tab gerade sichtbar geworden ist oder nicht.
     * Quelle: https://stackoverflow.com/a/9779971
     *
     * @param isVisibleToUser Indikator ob die aktuelle UI für den User sichtbar ist. Default ist True.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible()) {
            if (isVisibleToUser) {
                updateView();
                //todo nur updaten wenn etwas passiert ist
            }
        }
    }
}