package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public class DeleteExpenseMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "deleteAction";
    private static final String TAG = DeleteExpenseMenuItem.class.getSimpleName();

    private final IActionKey mActionKey;

    private OnSuccessCallback mCallback;
    private ExpenseRepository mExpenseRepo;
    private ChildExpenseRepository mChildExpenseRepo;

    public DeleteExpenseMenuItem(OnSuccessCallback callback) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_delete_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_delete_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload actionPayload, Context context) {
        initRepos(context);

        for (IRecyclerItem selectedExpense : actionPayload.getItems()) {

            if (selectedExpense instanceof ExpenseItem) {
                deleteExpense((ExpenseItem) selectedExpense);

                continue;
            }

            if (selectedExpense instanceof ChildExpenseItem) {
                deleteChild((ChildExpenseItem) selectedExpense);
            }
        }
    }

    private void initRepos(Context context) {
        mExpenseRepo = new ExpenseRepository(context);
        mChildExpenseRepo = new ChildExpenseRepository(context);
    }

    private void deleteChild(ChildExpenseItem item) {
        Booking child = item.getContent();

        try {
            mChildExpenseRepo.delete(child);

            if (null != mCallback) {
                mCallback.onSuccess(item);
            }
        } catch (CannotDeleteChildExpenseException e) {

            Log.e(TAG, "Could not delete ChildExpense " + child.getTitle(), e);
        }
    }

    private void deleteExpense(ExpenseItem item) {
        Booking expense = item.getContent();

        try {
            mExpenseRepo.delete(expense);

            if (null != mCallback) {
                mCallback.onSuccess(item);
            }

        } catch (CannotDeleteExpenseException e) {

            Log.e(TAG, "Could not delete Booking " + expense.getTitle(), e);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem deletedItem);
    }
}
