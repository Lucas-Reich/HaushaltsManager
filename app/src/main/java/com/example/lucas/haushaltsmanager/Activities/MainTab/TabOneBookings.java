package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.AddChildMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.CombineMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.DeleteMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ExtractMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.IMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.RecurringMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.TemplateMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.FABToolbarWithActionHandler;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarFABClickListener;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarItemClickListener;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectedRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.EndlessRecyclerViewScrollListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ExpenseListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar.RevertExpenseDeletionSnackbar;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TabOneBookings
        extends AbstractTab
        implements RecyclerItemClickListener.OnItemClickListener, OnFABToolbarItemClickListener, OnFABToolbarFABClickListener {

    private static final String TAG = TabOneBookings.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ExpenseListRecyclerViewAdapter mAdapter;
    private FABToolbarWithActionHandler mFABToolbar;
    private RevertExpenseDeletionSnackbar mRevertDeletionSnackbar;
    private ParentActivity mParent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParent = (ParentActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_recycler_view, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, this));

        updateView(rootView);

        mFABToolbar = new FABToolbarWithActionHandler(
                (FABToolbarLayout) rootView.findViewById(R.id.recycler_view_fab_toolbar)
        );

        mFABToolbar.setOnFabClickListener(this);

        setActionHandler();

        return rootView;
    }

    @Override
    public void updateView(View rootView) {
        mAdapter = new ExpenseListRecyclerViewAdapter(loadData(0));

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, final int offset, RecyclerView view) {
                // Methode muss darf erst nach einer bestimmten Zeit ausgeführt werden, da sonst die Liste falsch geupdated wird
                // Link: https://stackoverflow.com/a/39981688
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.insertAll(loadData(
                                offset
                        ));
                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        IRecyclerItem item = mAdapter.getItem(position);

        if (item instanceof DateItem || item instanceof AdItem)
            return;

        if (item instanceof ParentExpenseItem) {
            mAdapter.toggleExpansion(position);
            return;
        }

        if (mAdapter.getSelectedItemCount() == 0) {
            if (item instanceof ExpenseItem || item instanceof ChildItem) {
                updateItem(item);
            }
        } else {
            // If Item is an ExpenseItem and no Children are selected
            if (item instanceof ExpenseItem && mAdapter.getSelectedChildCount() == 0) {
                mAdapter.toggleSelection(item, position);
            }

            // If item is an ChildItem and no Expenses are selected
            if (item instanceof ChildItem && mAdapter.getSelectedItemsCount() == 0) {
                mAdapter.toggleSelection(item, position);
            }

            updateFABToolbar();
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        IRecyclerItem item = mAdapter.getItem(position);

        if (item instanceof DateItem || mAdapter.getSelectedItemCount() != 0)
            return;

        if (item instanceof ParentExpenseItem) {
            mAdapter.toggleExpansion(position);

            return;
        }

        mAdapter.toggleSelection(item, position);

        updateFABToolbar();
    }

    @Override
    public void onFabClick() {
        if (noAccountExists()) {
            Toast.makeText(getContext(), getString(R.string.no_account), Toast.LENGTH_SHORT).show();
            // TODO: Open dialog which prompts the user to create an account

            return;
        }

        Intent createExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
        createExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
        startActivity(createExpenseIntent);
    }

    @Override
    public void onFABMenuItemClick(IMenuItem actionHandler) {
        List<SelectedRecyclerItem> selectedItems = mAdapter.getSelectedItems();
        Collections.sort(selectedItems, new Comparator<SelectedRecyclerItem>() {
            @Override
            public int compare(SelectedRecyclerItem o1, SelectedRecyclerItem o2) {
                return Integer.compare(o2.getPosition(), o1.getPosition());
            }
        });

        ActionPayload actionPayload = new ActionPayload();
        actionPayload.setPayload(selectedItems);

        switch (actionHandler.getActionKey().getActionKey()) {
            case RecurringMenuItem.ACTION_KEY:
            case AddChildMenuItem.ACTION_KEY:
            case TemplateMenuItem.ACTION_KEY:
            case CombineMenuItem.ACTION_KEY:
            case ExtractMenuItem.ACTION_KEY:
                Log.i(TAG, String.format("Der ActionHandler %s wurde aufgerufen", actionHandler.getActionKey().getActionKey()));

                actionHandler.handleClick(actionPayload, mParent);
                break;
            case DeleteMenuItem.ACTION_KEY:
                Log.i(TAG, String.format("Der ActionHandler %s wurde aufgerufen", actionHandler.getActionKey().getActionKey()));

                initializeRevertDeletionSnackbar();

                DeleteMenuItem deleteAction = (DeleteMenuItem) actionHandler;
                deleteAction.handleClick(actionPayload, getContext());

                mRevertDeletionSnackbar.showSnackbar(
                        getView().findViewById(R.id.recycler_view_fab_toolbar)
                );
                break;
            default:
                Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
        }

        mParent.updateExpenses();

        mAdapter.clearSelections();

        mFABToolbar.toggleToolbarVisibility(false);
    }

    private List<IRecyclerItem> loadData(int offset) {
        List<ExpenseObject> visibleExpenses = mParent.getVisibleExpensesByOffsetWithParents(offset, 30);

        return transformExpenses(visibleExpenses);
    }

    private void setActionHandler() {
        mFABToolbar.addMenuItem(new ExtractMenuItem(new ExtractMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(int extractedItemPosition, ExpenseObject extractedExpense) {
                mAdapter.removeItem(extractedItemPosition);

                mAdapter.insertItem(new ExpenseItem(extractedExpense));
            }
        }), this);

        mFABToolbar.addMenuItem(new CombineMenuItem(new CombineMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(ParentExpenseObject combinedExpense, List<Integer> removedItemPositions) {
                for (Integer removedItemPosition : removedItemPositions) {
                    mAdapter.removeItem(removedItemPosition);
                }

                mAdapter.insertItem(new ParentExpenseItem(combinedExpense));
            }
        }), this);

        mFABToolbar.addMenuItem(new AddChildMenuItem(), this);

        mFABToolbar.addMenuItem(new DeleteMenuItem(new DeleteMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(IRecyclerItem deletedItem, int deletedItemPosition) {
                mAdapter.removeItem(deletedItemPosition);

                mRevertDeletionSnackbar.addItem(deletedItem);
            }
        }), this);

        mFABToolbar.addMenuItem(new TemplateMenuItem(new TemplateMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(Template template) {

                Toast.makeText(getContext(), R.string.saved_as_template, Toast.LENGTH_SHORT).show();
            }
        }), this);

        mFABToolbar.addMenuItem(new RecurringMenuItem(), this);
    }

    private void updateFABToolbar() {
        int selectedChildren = mAdapter.getSelectedChildCount();
        int selectedItems = mAdapter.getSelectedItemsCount();

        // Wenn kein Item ausgewählt ist
        if (selectedChildren == 0 && selectedItems == 0) {
            mFABToolbar.toggleToolbarVisibility(false);

            return;
        }

        // Wenn ein order mehrere Kinder ausgewählt sind
        if (selectedChildren > 0 && selectedItems == 0) {
            mFABToolbar.toggleToolbarVisibility(true);
            mFABToolbar.toggleMenuItemVisibility(ExtractMenuItem.ACTION_KEY, true);
            mFABToolbar.toggleMenuItemVisibility(AddChildMenuItem.ACTION_KEY, false);
            mFABToolbar.toggleMenuItemVisibility(CombineMenuItem.ACTION_KEY, false);

            return;
        }

        // Wenn ein Item ausgewählt ist
        if (selectedChildren == 0 && selectedItems == 1) {
            mFABToolbar.toggleToolbarVisibility(true);
            mFABToolbar.toggleMenuItemVisibility(ExtractMenuItem.ACTION_KEY, false);
            mFABToolbar.toggleMenuItemVisibility(AddChildMenuItem.ACTION_KEY, true);
            mFABToolbar.toggleMenuItemVisibility(CombineMenuItem.ACTION_KEY, false);

            return;
        }

        // Wenn mehrere Items ausgewählt sind
        if (selectedChildren == 0 && selectedItems > 1) {
            mFABToolbar.toggleToolbarVisibility(true);
            mFABToolbar.toggleMenuItemVisibility(ExtractMenuItem.ACTION_KEY, false);
            mFABToolbar.toggleMenuItemVisibility(AddChildMenuItem.ACTION_KEY, false);
            mFABToolbar.toggleMenuItemVisibility(CombineMenuItem.ACTION_KEY, true);
        }
    }

    private boolean noAccountExists() {
        UserSettingsPreferences userSettings = new UserSettingsPreferences(getContext());

        return null == userSettings.getActiveAccount();
    }

    private void updateItem(IRecyclerItem item) {
        if (item instanceof ChildItem || item instanceof ExpenseItem) {
            String intentMode = item instanceof ChildItem ? ExpenseScreen.INTENT_MODE_UPDATE_CHILD : ExpenseScreen.INTENT_MODE_UPDATE_PARENT;

            Intent updateBookingIntent = new Intent(getContext(), ExpenseScreen.class);
            updateBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, intentMode);
            updateBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, (ExpenseObject) item.getContent());

            startActivity(updateBookingIntent);
        }
    }

    private List<IRecyclerItem> transformExpenses(List<ExpenseObject> expenses) {
        List<IRecyclerItem> items = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent()) {
                items.add(new ParentExpenseItem(new ParentExpenseObject(expense, expense.getChildren())));
            } else {
                items.add(new ExpenseItem(expense));
            }
        }

        return items;
    }

    private void initializeRevertDeletionSnackbar() {
        mRevertDeletionSnackbar = new RevertExpenseDeletionSnackbar(getContext());
        mRevertDeletionSnackbar.setMessage(getString(R.string.revert_deletion));
        mRevertDeletionSnackbar.setOnExpenseRestoredListener(new RevertExpenseDeletionSnackbar.OnExpenseRestoredListener() {
            @Override
            public void onExpenseRestored(IRecyclerItem item) {
                mAdapter.insertItem(item);

                mParent.updateExpenses();
            }
        });
    }
}
