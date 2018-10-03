package com.example.lucas.haushaltsmanager.Activities.MainTab.TabOne;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabOneBookings extends Fragment implements FABToolbar.OnFabToolbarMenuItemClicked {
    private static final String TAG = TabOneBookings.class.getSimpleName();

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpListView;
    private ParentActivity mParent;

    private AccountRepository mAccountRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mBookingRepo;

    private FABToolbar mTabOneFabToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();

        mAccountRepo = new AccountRepository(getContext());
        mChildExpenseRepo = new ChildExpenseRepository(getContext());
        mBookingRepo = new ExpenseRepository(getContext());
    }

    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne indicators zu machen
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        mExpListView = rootView.findViewById(R.id.expandable_list_view);

        mExpListView.setBackgroundColor(Color.WHITE);

        setOnGroupClickListener();

        setOnChildClickListener();

        setOnItemLongClickListener();

        updateListView();

        mTabOneFabToolbar = new FABToolbar(
                (FABToolbarLayout) rootView.findViewById(R.id.fabtoolbar),
                getContext(),
                this
        );

        return rootView;
    }

    private List<ExpenseObject> getSelectedBookings(HashMap<ExpenseObject, List<ExpenseObject>> bookings) {
        List<ExpenseObject> selectedBookings = new ArrayList<>();
        for (Map.Entry<ExpenseObject, List<ExpenseObject>> booking : bookings.entrySet()) {
            if (booking.getValue().size() != 0) {
                selectedBookings.addAll(booking.getValue());
            } else {
                selectedBookings.add(booking.getKey());
            }
        }

        return selectedBookings;
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

                mListAdapter.selectItem(groupPosition, childPosition != -1 ? childPosition : null);

                disableLongClick();

                mTabOneFabToolbar.showToolbar();

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

                if (noBookingsSelected() && isEditable(groupExpense)) {

                    Intent updateParentExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                    updateParentExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_UPDATE_PARENT);
                    updateParentExpenseIntent.putExtra(ExpenseScreen.INTENT_BOOKING, groupExpense);

                    startActivity(updateParentExpenseIntent);

                    return true;
                }

                switch (groupExpense.getExpenseType()) {
                    case PARENT_EXPENSE:
                        if (noBookingsSelected())
                            return false;
                    case NORMAL_EXPENSE:
                    case CHILD_EXPENSE:
                        if (mListAdapter.isItemSelected(groupPosition, null)) {
                            mListAdapter.deselectItem(groupPosition, null);

                            if (noBookingsSelected())
                                enableLongClick();
                        } else
                            mListAdapter.selectItem(groupPosition, null);

                        changeToolbarMenuItemOne(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                        return groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE;
                    default:
                        return true;
                }
            }
        });
    }

    /**
     * Methode um einen ChildClickListener auf die ExpandableListView zu setzen.
     */
    private void setOnChildClickListener() {
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                ExpenseObject childExpense = (ExpenseObject) mListAdapter.getChild(groupPosition, childPosition);

                if (noBookingsSelected()) {

                    Intent updateChildExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
                    updateChildExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_UPDATE_CHILD);
                    updateChildExpenseIntent.putExtra(ExpenseScreen.INTENT_BOOKING, childExpense);

                    startActivity(updateChildExpenseIntent);

                    return true;
                }

                if (mListAdapter.isItemSelected(groupPosition, childPosition)) {
                    mListAdapter.deselectItem(groupPosition, childPosition);

                    if (noBookingsSelected())
                        enableLongClick();
                } else
                    mListAdapter.selectItem(groupPosition, childPosition);

                //todo update toolbar items
                changeToolbarMenuItemOne(mListAdapter.getSelectedGroupCount(), mListAdapter.getSelectedChildCount(), mListAdapter.getSelectedParentCount());
                return true;
            }
        });
    }

    private boolean noBookingsSelected() {
        return mListAdapter.getSelectedBookingsCount() == 0;
    }

    private boolean isEditable(ExpenseObject expense) {
        return !isParent(expense) && !isDateSep(expense);
    }

    private boolean isParent(ExpenseObject expense) {
        return expense.getExpenseType() == ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE;
    }

    private boolean isDateSep(ExpenseObject expense) {
        return expense.getExpenseType() == ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER;
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
     * Methode um die ListView in ihren Ausgangszustand zurückzusetzen.
     */
    private void resetListView() {
        mListAdapter.deselectAll();

        updateListView();

        enableLongClick();
    }

    /**
     * Methode um die ExpandableListView nach einer Änderung neu anzuzeigen.
     */
    public void updateListView() {

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
    private void changeToolbarMenuItemOne(int selectedGroups, int selectedChildren, int selectedParents) {

        //wenn keine buchung ausgewählt ist sollen die Buttons in den Normalzustand zurückkehren
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 0)
            mTabOneFabToolbar.hideToolbar();

        //wenn eine Buchung ausgewählt ist sollen die Buttons add child und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups == 1)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_add_child_white, "", FABToolbar.MENU_ADD_CHILD_ACTION);

        //wenn zwei oder mehr Buchungen ausgewählt sind sollen die Buttons Combine und Delete angezeigt werden
        if (selectedChildren == 0 && selectedParents == 0 && selectedGroups > 1)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_combine_white, "", FABToolbar.MENU_COMBINE_ACTION);

        //wenn eine Buchung und ein Parent ausgewählt sind sollen der Button AddToParent angezezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups > 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_merge_bookings_white, "", FABToolbar.MENU_ADD_CHILD_ACTION);

        //wenn eine oder mehrere ChildBuchung/en ausgewählt ist/sind sollen die Buttons extract und deleteAll angezeigt werden
        if (selectedChildren > 0 && selectedParents == 0 && selectedGroups == 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_extract_child_white, "", FABToolbar.MENU_EXTRACT_ACTION);

        //wenn eine ParentBuchung ausgewählt ist sollen die Buttons add Child und deleteAll angezeigt werden
        if (selectedChildren == 0 && selectedParents == 1 && selectedGroups == 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_add_child_white, "", FABToolbar.MENU_ADD_CHILD_ACTION);

        //wenn eine oder mehrere ParentBuchungen ausgewählt sind sollen die Buttons combine und deleteAll angezeigt werden
        if (selectedChildren == 0 && selectedParents > 1 && selectedGroups == 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_merge_bookings_white, "", FABToolbar.MENU_COMBINE_ACTION);
    }

    @Override
    public void onFabClick() {
        if (mAccountRepo.getAll().size() == 0) {//todo elegantere Möglichkeit finden den user zu zwingen ein Konto zu erstellen, bevor er eine Buchung erstellt
            Toast.makeText(getContext(), getString(R.string.no_account), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent createExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
        createExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
        getContext().startActivity(createExpenseIntent);
    }

    @Override
    public void onFabToolbarMenuItemClicked(String tag) {
        HashMap<ExpenseObject, List<ExpenseObject>> selectedBookings = mListAdapter.getSelectedBookings();

        switch (tag) {
            case FABToolbar.MENU_COMBINE_ACTION:
                combineBookingsAction();
                break;
            case FABToolbar.MENU_EXTRACT_ACTION:
                extractBookingsAction(selectedBookings);
                break;
            case FABToolbar.MENU_ADD_CHILD_ACTION:
                addChildAction((ExpenseObject) selectedBookings.keySet().toArray()[0]);
            case FABToolbar.MENU_RECURRING_ACTION:
                saveAsRecurringAction();
                break;
            case FABToolbar.MENU_TEMPLATE_ACTION:
                saveAsTemplateAction();
                break;
            case FABToolbar.MENU_DELETE_ACTION:
                deleteBookingsAction(selectedBookings);
                showRevertDeletionSnackbar(
                        selectedBookings,
                        R.string.revert_deletion,
                        R.string.bookings_successfully_restored
                );
                break;
            default:
                Log.i(TAG, "Found not existing Menu item " + tag);
                Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void combineBookingsAction() {
        Bundle bundle = new Bundle();
        bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.input_title));

        BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
        textInputDialog.setArguments(bundle);
        textInputDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

            @Override
            public void onTextInput(String title) {
                ExpenseObject parentBooking = mChildExpenseRepo.combineExpenses(getSelectedBookings(mListAdapter.getSelectedBookings()));

                try {
                    parentBooking.setTitle(title);
                    mBookingRepo.update(parentBooking);

                    mParent.updateExpenses();
                } catch (ExpenseNotFoundException e) {

                    Log.e(TAG, getString(R.string.could_not_update_booking), e);
                    Toast.makeText(getContext(), R.string.could_not_update_booking, Toast.LENGTH_SHORT).show();
                    //todo fehlerbehandlung
                }

                mParent.updateExpenses();

                resetListView();

                mTabOneFabToolbar.hideToolbar();
            }
        });

        textInputDialog.show(getActivity().getFragmentManager(), "");
    }

    private void extractBookingsAction(HashMap<ExpenseObject, List<ExpenseObject>> expenses) {
        for (Map.Entry<ExpenseObject, List<ExpenseObject>> bookings : expenses.entrySet()) {
            for (ExpenseObject child : bookings.getValue()) {
                try {
                    mChildExpenseRepo.extractChildFromBooking(child);
                } catch (ChildExpenseNotFoundException e) {
                    //todo was soll passieren wenn eine KindBuchung nicht in der Datenbank gefunden werden konnte
                }
            }
        }

        mParent.updateExpenses();

        mTabOneFabToolbar.hideToolbar();

        resetListView();
    }

    private void addChildAction(ExpenseObject parent) {
        Intent createChildToBookingIntent = new Intent(getContext(), ExpenseScreen.class);
        createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_ADD_CHILD);
        createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, parent);

        resetListView();

        mTabOneFabToolbar.hideToolbar();

        getContext().startActivity(createChildToBookingIntent);
    }

    private void saveAsTemplateAction() {
        ExpenseObject expense = ExpenseObject.createDummyExpense();

        //todo wenn mehr als eine Buchung markiert sind dann soll der template button versteckt werden
        TemplateRepository templateRepository = new TemplateRepository(getContext());
        templateRepository.insert(new Template(expense));
    }

    private void saveAsRecurringAction() {
        ExpenseObject expense = ExpenseObject.createDummyExpense();

        long start = 0;
        long end = 0;
        int freq = 1;

        //todo wenn mehr als eine Buchung markiert ist dann soll der recurring button versteckt werden
        //todo zeige einen alert dialog an welcher den user nach den zeiträumen fragt
        RecurringBookingRepository recurringBookingRepository = new RecurringBookingRepository(getContext());
        recurringBookingRepository.insert(expense, start, freq, end);
    }

    private void deleteBookingsAction(HashMap<ExpenseObject, List<ExpenseObject>> bookings) {
        for (Map.Entry<ExpenseObject, List<ExpenseObject>> booking : bookings.entrySet()) {
            if (booking.getValue().size() != 0) {
                for (ExpenseObject child : booking.getValue()) {
                    try {
                        mChildExpenseRepo.delete(child);
                    } catch (CannotDeleteChildExpenseException e) {
                        //todo was soll ich machen wenn ein kind nicht gelöscht werden konnte
                    }
                }
            } else {
                try {
                    mBookingRepo.delete(booking.getKey());
                } catch (CannotDeleteExpenseException e) {
                    //todo was soll ich machen wenn ich eine group buchung nicht gelöscht werden konnte
                }
            }
        }
    }

    /**
     * Methode um eine Snackbar anzuzeigen.
     *
     * @param bookings       Buchungen die gelöscht wurden
     * @param message        Nachricht die in der Snackbar angezeigt werden soll
     * @param successMessage Nachricht die angezeigt wird wenn alles erfolgreich bearbeitet wurde
     */
    private void showRevertDeletionSnackbar(HashMap<ExpenseObject, List<ExpenseObject>> bookings, @StringRes int message, @StringRes final int successMessage) {
        CoordinatorLayout coordinatorLayout = getView().findViewById(R.id.tab_one_bookings_layout);

        RevertExpenseDeletionSnackbar snackbar = new RevertExpenseDeletionSnackbar(bookings, getContext());
        snackbar.setOnRestoredActionListener(new RevertExpenseDeletionSnackbar.OnRestoredActionListener() {
            @Override
            public void onExpensesRestored() {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();

                mParent.updateExpenses();

                updateListView();
            }
        });
        snackbar.showSnackbar(coordinatorLayout, getString(message));
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
                updateListView();
                //todo nur updaten wenn etwas passiert ist
            }
        }
    }
}