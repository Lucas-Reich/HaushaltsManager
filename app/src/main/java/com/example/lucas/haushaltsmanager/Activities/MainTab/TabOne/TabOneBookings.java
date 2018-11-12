package com.example.lucas.haushaltsmanager.Activities.MainTab.TabOne;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.EditRecurringBooking;
import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Activities.MainTab.AbstractMainTab;
import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.ExpListViewSelectedItem;
import com.example.lucas.haushaltsmanager.ExpandableListAdapter;
import com.example.lucas.haushaltsmanager.ExpandableListAdapterCreator;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.Calendar;
import java.util.List;

public class TabOneBookings extends AbstractMainTab implements FABToolbar.OnFabToolbarMenuItemClicked {
    private static final String TAG = TabOneBookings.class.getSimpleName();

    private ExpandableListAdapter mListAdapter;
    private ExpandableListView mExpListView;
    private ParentActivity mParent;
    private UserSettingsPreferences mUserSettings;
    private RevertExpenseDeletionSnackbar mRevertDeletionSnackbar;

    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mExpenseRepo;

    private FABToolbar mTabOneFabToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();

        mUserSettings = new UserSettingsPreferences(getContext());

        mChildExpenseRepo = new ChildExpenseRepository(getContext());
        mExpenseRepo = new ExpenseRepository(getContext());
    }

    /**
     * https://www.captechconsulting.com/blogs/android-expandablelistview-magic
     * Anleitung um eine ExpandableListView ohne Indikatoren zu machen
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        mExpListView = rootView.findViewById(R.id.expandable_list_view);

        mExpListView.setBackgroundColor(Color.WHITE);

        setOnGroupClickListener();

        setOnChildClickListener();

        setOnItemLongClickListener();

        updateView();

        mTabOneFabToolbar = new FABToolbar(
                (FABToolbarLayout) rootView.findViewById(R.id.fabtoolbar),
                getContext(),
                this
        );

        return rootView;
    }

    private void setOnItemLongClickListener() {
        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                int childPosition = ExpandableListView.getPackedPositionChild(id);

                if (isDateSep((ExpenseObject) mListAdapter.getGroup(groupPosition)))
                    return true;

                mListAdapter.selectItem(
                        groupPosition,
                        childPosition
                );

                disableListViewLongClick();

                mTabOneFabToolbar.showToolbar();

                updateToolbarItemOne(
                        mListAdapter.getSelectedGroupsCount(),
                        mListAdapter.getSelectedChildrenCount()
                );

                return true;
            }
        });
    }

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
                        if (mListAdapter.isItemSelected(groupPosition, -1)) {
                            mListAdapter.unselectItem(groupPosition, -1);

                            if (noBookingsSelected())
                                enableListViewLongClick();
                        } else
                            mListAdapter.selectItem(groupPosition, -1);

                        updateToolbarItemOne(
                                mListAdapter.getSelectedGroupsCount(),
                                mListAdapter.getSelectedChildrenCount()
                        );
                        return groupExpense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE;
                    default:
                        return true;
                }
            }
        });
    }

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
                    mListAdapter.unselectItem(groupPosition, childPosition);

                    if (noBookingsSelected())
                        enableListViewLongClick();
                } else
                    mListAdapter.selectItem(groupPosition, childPosition);

                updateToolbarItemOne(
                        mListAdapter.getSelectedGroupsCount(),
                        mListAdapter.getSelectedChildrenCount()
                );
                return true;
            }
        });
    }

    private boolean noBookingsSelected() {
        return mListAdapter.getSelectedItemCount() == 0;
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

    private void disableListViewLongClick() {
        mExpListView.setLongClickable(false);
    }

    private void enableListViewLongClick() {
        mExpListView.setLongClickable(true);
    }

    private void resetListView() {
        mListAdapter.unselectAll();

        updateView();

        enableListViewLongClick();
    }

    public void updateView() {

        mListAdapter = new ExpandableListAdapterCreator(
                mParent.getExpenses(getFirstOfMonth(), getLastOfMonth()),
                mParent.getActiveAccounts(),
                getContext()
        ).getExpandableListAdapter();

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
    }

    private Calendar getFirstOfMonth() {
        Calendar firstOfMonth = Calendar.getInstance();
        firstOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        firstOfMonth.set(Calendar.MINUTE, 0);
        firstOfMonth.set(Calendar.SECOND, 1);
        firstOfMonth.set(Calendar.DAY_OF_MONTH, 1);

        return firstOfMonth;
    }

    private Calendar getLastOfMonth() {
        int lastDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar lastOfMonth = Calendar.getInstance();
        lastOfMonth.set(Calendar.DAY_OF_MONTH, lastDayMonth);

        return lastOfMonth;
    }

    private void updateToolbarItemOne(int selectedParentCount, int selectedChildCount) {

        if (selectedChildCount + selectedParentCount > 1) {
            mTabOneFabToolbar.hideAction(FABToolbar.MENU_TEMPLATE_ACTION);
            mTabOneFabToolbar.hideAction(FABToolbar.MENU_RECURRING_ACTION);
        } else {
            mTabOneFabToolbar.showAction(FABToolbar.MENU_TEMPLATE_ACTION);
            mTabOneFabToolbar.showAction(FABToolbar.MENU_RECURRING_ACTION);
        }

        //Wenn keine Buchung ausgewählt ist dann soll der FAB angezeigt werden
        if (selectedParentCount == 0 && selectedChildCount == 0)
            mTabOneFabToolbar.hideToolbar();

        //Wenn eine Buchung ausgewählt ist soll der ADD_CHILD Button angezeigt werden
        if (selectedParentCount == 1 && selectedChildCount == 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_add_child_white, "", FABToolbar.MENU_ADD_CHILD_ACTION);

        //Wenn mind. zwei Buchungen ausgewählt sind soll der MERGE Button angezeigt werden
        if (selectedParentCount > 1 && selectedChildCount == 0)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_merge_bookings_white, "", FABToolbar.MENU_COMBINE_ACTION);

        //Wenn mind. eine Buchung und mind. ein Parent ausgewählt ist soll der MERGE Button angezeigt werden
        if (selectedParentCount >= 1 && selectedChildCount >= 1)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_merge_bookings_white, "", FABToolbar.MENU_COMBINE_ACTION);

        //Wenn mind. ein Kind ausgewählt ist soll der EXTRACT Button angezeigt werden
        if (selectedParentCount == 0 && selectedChildCount >= 1)
            mTabOneFabToolbar.changeToolbarItemOne(R.drawable.ic_extract_child_white, "", FABToolbar.MENU_EXTRACT_ACTION);
    }

    @Override
    public void onFabClick() {
        if (null == mUserSettings.getActiveAccount()) {
            Toast.makeText(getContext(), getString(R.string.no_account), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent createExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
        createExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
        startActivity(createExpenseIntent);
    }

    @Override
    public void onFabToolbarMenuItemClicked(String actionTag) {
        List<ExpListViewSelectedItem> selectedItems = mListAdapter.getSelectedItems();

        switch (actionTag) {
            case FABToolbar.MENU_COMBINE_ACTION:
                combineBookingsAction(selectedItems);
                break;
            case FABToolbar.MENU_EXTRACT_ACTION:
                extractBookingsAction(selectedItems);
                break;
            case FABToolbar.MENU_ADD_CHILD_ACTION:
                addChildAction(selectedItems.get(0).getItem());
                break;
            case FABToolbar.MENU_RECURRING_ACTION:
                saveAsRecurringAction(selectedItems.get(0).getItem());
                break;
            case FABToolbar.MENU_TEMPLATE_ACTION:
                saveAsTemplateAction(selectedItems.get(0).getItem());
                break;
            case FABToolbar.MENU_DELETE_ACTION:
                initializeRevertDeletionSnackbar(
                        R.string.revert_deletion,
                        R.string.bookings_successfully_restored
                );
                deleteBookingsAction(selectedItems);
                break;
            default:
                Log.e(TAG, "Found not existing Menu item " + actionTag);
                Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void combineBookingsAction(final List<ExpListViewSelectedItem> selectedItems) {
        Bundle bundle = new Bundle();
        bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.input_title));

        BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
        textInputDialog.setArguments(bundle);
        textInputDialog.setOnTextInputListener(getOnTextInputListener(selectedItems));
        textInputDialog.show(getActivity().getFragmentManager(), "");
    }

    private BasicTextInputDialog.OnTextInput getOnTextInputListener(final List<ExpListViewSelectedItem> selectedItems) {
        return new BasicTextInputDialog.OnTextInput() {
            @Override
            public void onTextInput(String combinedExpenseTitle) {
                ExpenseObject parent = ExpenseObject.createDummyExpense();
                parent.setTitle(combinedExpenseTitle);

                //TODO Wenn die angegebene Buchung eine ParentBuchung ist müssen anstatt der Buchung die KindBuchungen zusammengefügt werden
                for (ExpListViewSelectedItem selectedItem : selectedItems) {
                    try {
                        if (selectedItem.isParent())
                            mExpenseRepo.delete(selectedItem.getItem());
                        else
                            mChildExpenseRepo.delete(selectedItem.getItem());

                        parent.addChild(selectedItem.getItem());
                    } catch (CannotDeleteChildExpenseException e) {

                        //TODO was soll passieren
                        Log.e(TAG, "Could not delete ChildExpense " + selectedItem.getItem().getTitle());
                    } catch (CannotDeleteExpenseException e) {

                        //TODO was soll passieren
                        Log.e(TAG, "Could not delete Expense " + selectedItem.getItem().getTitle());
                    }
                }

                mExpenseRepo.insert(parent);

                mParent.updateExpenses();

                resetListView();

                mTabOneFabToolbar.hideToolbar();
            }
        };
    }

    private void addChildAction(ExpenseObject parentExpense) {
        Intent createChildToBookingIntent = new Intent(getContext(), ExpenseScreen.class);
        createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_ADD_CHILD);
        createChildToBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, parentExpense);

        resetListView();

        mTabOneFabToolbar.hideToolbar();

        startActivity(createChildToBookingIntent);
    }

    private void saveAsRecurringAction(ExpenseObject recurringExpense) {
        Intent recurringIntent = new Intent(getContext(), EditRecurringBooking.class);
        recurringIntent.putExtra(EditRecurringBooking.INTENT_BOOKING, recurringExpense);

        resetListView();

        mTabOneFabToolbar.hideToolbar();

        startActivity(recurringIntent);
    }

    private void saveAsTemplateAction(ExpenseObject templateExpense) {
        TemplateRepository templateRepo = new TemplateRepository(getContext());
        templateRepo.insert(new Template(templateExpense));

        Toast.makeText(getContext(), R.string.saved_as_template, Toast.LENGTH_SHORT).show();

        resetListView();

        mTabOneFabToolbar.hideToolbar();
    }

    private void extractBookingsAction(List<ExpListViewSelectedItem> selectedItems) {
        for (ExpListViewSelectedItem selectedItem : selectedItems) {
            try {

                mChildExpenseRepo.extractChildFromBooking(selectedItem.getItem());
            } catch (ChildExpenseNotFoundException e) {

                Log.e(TAG, "Could not extract ChildExpense " + selectedItem.getItem().getTitle(), e);
            }
        }

        mParent.updateExpenses();

        resetListView();

        mTabOneFabToolbar.hideToolbar();
    }

    private void deleteBookingsAction(List<ExpListViewSelectedItem> selectedItems) {
        for (ExpListViewSelectedItem selectedItem : selectedItems) {
            try {
                if (selectedItem.isParent()) {
                    for (ExpenseObject child : selectedItem.getItem().getChildren()) {
                        mChildExpenseRepo.delete(child);
                        mRevertDeletionSnackbar.addItem(new ExpListViewSelectedItem(
                                child,
                                selectedItem.getItem()
                        ));
                    }
                } else {
                    mChildExpenseRepo.delete(selectedItem.getItem());
                    mRevertDeletionSnackbar.addItem(selectedItem);
                }

                mRevertDeletionSnackbar.addItem(selectedItem);

            } catch (CannotDeleteChildExpenseException e) {

                //todo was soll ich machen wenn eine KindBuchung nicht gelöscht werden konnte
                Log.e(TAG, "Could not delete ChildExpense " + selectedItem.getItem().getTitle(), e);
            }
        }

        mRevertDeletionSnackbar.showSnackbar(
                getView().findViewById(R.id.tab_one_bookings_layout)
        );

        mParent.updateExpenses();

        resetListView();

        mTabOneFabToolbar.hideToolbar();
    }

    private void initializeRevertDeletionSnackbar(@StringRes int message, @StringRes final int successMessage) {
        mRevertDeletionSnackbar = new RevertExpenseDeletionSnackbar(getContext());
        mRevertDeletionSnackbar.setMessage(getString(message));
        mRevertDeletionSnackbar.setOnRestoredActionListener(new RevertExpenseDeletionSnackbar.OnRestoredActionListener() {
            @Override
            public void onExpensesRestored() {
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();

                mParent.updateExpenses();

                updateView();
            }
        });
    }
}