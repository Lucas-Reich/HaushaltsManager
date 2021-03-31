package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.TemplateBooking;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.AddChildMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.CombineMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.DeleteExpenseMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ExtractMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.IMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.RecurringMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.TemplateMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.FABToolbarWithActionHandler;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarFABClickListener;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarItemClickListener;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.EndlessRecyclerViewScrollListener;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildExpenseItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ExpenseItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentExpenseItem.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar.RevertExpenseDeletionSnackbar;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.List;

public class TabOneBookings extends AbstractTab implements
        RecyclerItemClickListener.OnRecyclerItemClickListener,
        OnFABToolbarItemClickListener,
        OnFABToolbarFABClickListener {

    private static final String TAG = TabOneBookings.class.getSimpleName();
    private static final int BATCH_SIZE = 30;

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
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, this));

        updateView(rootView);

        mFABToolbar = new FABToolbarWithActionHandler(
                (FABToolbarLayout) rootView.findViewById(R.id.recycler_view_fab_toolbar)
        );

        mFABToolbar.setOnFabClickListener(this);

        configureFabToolbar();

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
    public void onFabClick() {
        if (noAccountExists()) {
            Toast.makeText(getContext(), getString(R.string.no_account), Toast.LENGTH_SHORT).show();
            // TODO: Open dialog which prompts the user to add an account

            return;
        }

        Intent createExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
        createExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
        startActivity(createExpenseIntent);
    }

    @Override
    public void onFABMenuItemClick(IMenuItem actionHandler) {
        ActionPayload actionPayload = new ActionPayload();
        actionPayload.setPayload(mAdapter.getSelectedItems());

        switch (actionHandler.getActionKey().toString()) {
            case RecurringMenuItem.ACTION_KEY:
            case AddChildMenuItem.ACTION_KEY:
            case TemplateMenuItem.ACTION_KEY:
            case CombineMenuItem.ACTION_KEY:
            case ExtractMenuItem.ACTION_KEY:
                Log.i(TAG, String.format("Der ActionHandler '%s' wurde aufgerufen", actionHandler.getActionKey().toString()));

                actionHandler.handleClick(actionPayload, mParent);
                break;
            case DeleteExpenseMenuItem.ACTION_KEY:
                Log.i(TAG, String.format("Der ActionHandler '%s' wurde aufgerufen", actionHandler.getActionKey().toString()));

                initializeRevertDeletionSnackbar();

                DeleteExpenseMenuItem deleteAction = (DeleteExpenseMenuItem) actionHandler;
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

    @Override
    public void onClick(View v, IRecyclerItem item, int position) {
        if (item instanceof DateItem) {
            return;
        }

        if (item instanceof ParentExpenseItem) {
            mAdapter.toggleExpansion(position);
            return;
        }

        if (!mAdapter.isInSelectionMode()) {
            if (item instanceof ExpenseItem || item instanceof ChildExpenseItem) {
                updateItem(item);
            }
        } else {
            if (mAdapter.isItemSelected(item)) {
                mAdapter.unselectItem(item, position);
            } else {
                mAdapter.selectItem(item, position);
            }

            updateFABToolbar();
        }
    }

    @Override
    public void onLongClick(View v, IRecyclerItem item, int position) {
        if (item instanceof DateItem || mAdapter.getSelectedItemCount() != 0)
            return;

        if (item instanceof ParentExpenseItem) {
            mAdapter.toggleExpansion(position);

            return;
        }

        mAdapter.selectItem(item, position);

        updateFABToolbar();
    }

    private boolean noAccountExists() {
        UserSettingsPreferences userSettings = new UserSettingsPreferences(mParent);

        return null == userSettings.getActiveAccount();
    }

    private List<IRecyclerItem> loadData(int offset) {
        List<ExpenseObject> visibleExpenses = mParent.getVisibleExpensesByOffsetWithParents(offset, BATCH_SIZE);

        return ItemCreator.createExpenseItems(visibleExpenses);
    }

    private void configureFabToolbar() {
        mFABToolbar.addMenuItem(new ExtractMenuItem(new ExtractMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(IRecyclerItem extractedItem, ExpenseObject extractedExpense) {
                mAdapter.removeItem(extractedItem);

                mAdapter.insertItem(new ExpenseItem(extractedExpense, (DateItem) extractedItem.getParent().getParent()));
            }
        }), this);

        mFABToolbar.addMenuItem(new CombineMenuItem(new CombineMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(ParentExpenseObject combinedExpense, List<IRecyclerItem> removedItems) {
                for (IRecyclerItem removedItem : removedItems) {
                    mAdapter.removeItem(removedItem);
                }

                mAdapter.insertItem(new ParentExpenseItem(combinedExpense, (DateItem) removedItems.get(0).getParent()));

                mParent.updateExpenses();
            }
        }), this);

        mFABToolbar.addMenuItem(new AddChildMenuItem(), this);

        mFABToolbar.addMenuItem(new DeleteExpenseMenuItem(new DeleteExpenseMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(IRecyclerItem deletedItem) {
                mAdapter.removeItem(deletedItem);

                mRevertDeletionSnackbar.addItem(deletedItem);
            }
        }), this);

        mFABToolbar.addMenuItem(new TemplateMenuItem(new TemplateMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(TemplateBooking templateBooking) {

                Toast.makeText(getContext(), R.string.saved_as_template, Toast.LENGTH_SHORT).show();
            }
        }), this);

        mFABToolbar.addMenuItem(new RecurringMenuItem(), this);
    }

    private void updateFABToolbar() {
        // TODO: Kann ich hierfür eine smarte Lösung finden.
        //  Kann ich das auch mit SelectionRules machen, wie sie schon für die RecyclerView genutzt werden.
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

    private void updateItem(IRecyclerItem item) {
        if (item instanceof ChildExpenseItem || item instanceof ExpenseItem) {
            String intentMode = item instanceof ChildExpenseItem ? ExpenseScreen.INTENT_MODE_UPDATE_CHILD : ExpenseScreen.INTENT_MODE_UPDATE_PARENT;

            Intent updateBookingIntent = new Intent(getContext(), ExpenseScreen.class);
            updateBookingIntent.putExtra(ExpenseScreen.INTENT_MODE, intentMode);
            updateBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, (ExpenseObject) item.getContent());

            startActivity(updateBookingIntent);
        }
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
