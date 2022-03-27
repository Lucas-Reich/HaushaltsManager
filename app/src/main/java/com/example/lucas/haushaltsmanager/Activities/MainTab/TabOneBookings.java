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

import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.DragAndDropActivity;
import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
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
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.EndlessRecyclerViewScrollListener;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.ExpenseListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar.RevertExpenseDeletionSnackbar;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseFilter;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingWithoutCategory;

import java.util.ArrayList;
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
    private ActiveAccountsPreferences activeAccounts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activeAccounts = new ActiveAccountsPreferences(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_one_bookings, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), mRecyclerView, this));

        updateView(rootView);

        mFABToolbar = new FABToolbarWithActionHandler(rootView.findViewById(R.id.recycler_view_fab_toolbar));

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
                mRecyclerView.post(() -> mAdapter.insertAll(loadData(offset)));
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

//        Intent createExpenseIntent = new Intent(getContext(), ExpenseScreen.class);
//        createExpenseIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_CREATE_BOOKING);
//        startActivity(createExpenseIntent);

        startActivity(new Intent(getContext(), DragAndDropActivity.class));
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

                actionHandler.handleClick(actionPayload, getContext());
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

        mAdapter.clearSelection();

        mFABToolbar.toggleToolbarVisibility(false);
    }

    @Override
    public void onClick(View v, IRecyclerItem item) {
        if (item instanceof DateItem) {
            return;
        }

        if (item instanceof ParentBookingItem) {
            mAdapter.toggleExpansion(item);
            return;
        }

        if (!mAdapter.isInSelectionMode()) {
            if (item instanceof ExpenseItem || item instanceof ChildExpenseItem) {
                updateItem(item);
            }
        } else {
            if (mAdapter.isSelected(item)) {
                mAdapter.unselect(item);
            } else {
                mAdapter.select(item);
            }

            updateFABToolbar();
        }
    }

    @Override
    public void onLongClick(View v, IRecyclerItem item) {
        if (item instanceof DateItem || mAdapter.getSelectedItemCount() != 0) {
            return;
        }

        if (item instanceof ParentBookingItem) {
            mAdapter.toggleExpansion(item);

            return;
        }

        mAdapter.select(item);

        updateFABToolbar();
    }

    public boolean noAccountExists() {
        return false; // TODO: Check if at least one account exists
    }

    private List<Booking> getVisibleExpenses(int offset) {
        BookingDAO bookingRepository = AppDatabase.getDatabase(getContext()).bookingDAO();
        List<Booking> bookings = bookingRepository.getAll();

        // TODO: Get ParentBookings

        List<Booking> visibleExpenses = new ExpenseFilter().byAccount(
                bookings, // TODO: How to add parent bookings?
                activeAccounts.getActiveAccounts()
        );

        if (visibleExpenses.size() <= offset) {
            return new ArrayList<>();
        }

        if (visibleExpenses.size() < (offset + BATCH_SIZE)) {
            return visibleExpenses.subList(offset, visibleExpenses.size());
        }

        return visibleExpenses.subList(offset, offset + BATCH_SIZE);
    }

    private List<IRecyclerItem> loadData(int offset) {
        List<Booking> visibleExpenses = getVisibleExpenses(offset);

        return ItemCreator.createBookingItems(visibleExpenses);
    }

    private void configureFabToolbar() {
        mFABToolbar.addMenuItem(new ExtractMenuItem((extractedItem, extractedExpense) -> {
            mAdapter.remove(extractedItem);

            mAdapter.insert(new ExpenseItem(extractedExpense, (DateItem) extractedItem.getParent().getParent()));
        }), this);

        mFABToolbar.addMenuItem(new CombineMenuItem((combinedExpense, removedItems) -> {
            for (IRecyclerItem removedItem : removedItems) {
                mAdapter.remove(removedItem);
            }

            mAdapter.insert(new ParentBookingItem(combinedExpense, (DateItem) removedItems.get(0).getParent()));
        }), this);

        mFABToolbar.addMenuItem(new AddChildMenuItem(), this);

        mFABToolbar.addMenuItem(new DeleteExpenseMenuItem(deletedItem -> {
            mAdapter.remove(deletedItem);

            mRevertDeletionSnackbar.addItem(deletedItem);
        }), this);

        mFABToolbar.addMenuItem(new TemplateMenuItem(templateBooking -> {
            Toast.makeText(TabOneBookings.this.getContext(), R.string.saved_as_template, Toast.LENGTH_SHORT).show();
        }, AppDatabase.getDatabase(getContext()).templateBookingDAO()), this);

        mFABToolbar.addMenuItem(new RecurringMenuItem(), this);
    }

    private void updateFABToolbar() {
        // TODO: Kann ich hierfür eine smarte Lösung finden.
        //  Kann ich das auch mit SelectionRules machen, wie sie schon für die RecyclerView genutzt werden.
        int selectedChildren = mAdapter.getSelectedChildCount();
        int selectedItems = mAdapter.getSelectedParentCount();

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
            updateBookingIntent.putExtra(ExpenseScreen.INTENT_BOOKING, (Booking) item.getContent());

            startActivity(updateBookingIntent);
        }
    }

    private void initializeRevertDeletionSnackbar() {
        mRevertDeletionSnackbar = new RevertExpenseDeletionSnackbar(getContext());
        mRevertDeletionSnackbar.setMessage(getString(R.string.revert_deletion));
        mRevertDeletionSnackbar.setOnExpenseRestoredListener(item -> mAdapter.insert(item));
    }
}
