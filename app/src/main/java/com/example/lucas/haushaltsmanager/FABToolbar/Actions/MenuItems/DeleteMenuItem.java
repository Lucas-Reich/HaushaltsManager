package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectedRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

public class DeleteMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "deleteAction";
    private static final String TAG = DeleteMenuItem.class.getSimpleName();

    private final IActionKey mActionKey;

    private OnSuccessCallback mCallback;
    private ExpenseRepository mExpenseRepo;
    private ChildExpenseRepository mChildExpenseRepo;

    public DeleteMenuItem(OnSuccessCallback callback) {
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

        for (SelectedRecyclerItem selectedExpense : actionPayload.getItems()) {

            if (selectedExpense.getItem() instanceof ExpenseItem) {
                deleteExpense(
                        (ExpenseItem) selectedExpense.getItem(),
                        selectedExpense.getPosition()
                );

                continue;
            }

            if (selectedExpense.getItem() instanceof ChildItem) {
                deleteChild(
                        (ChildItem) selectedExpense.getItem(),
                        selectedExpense.getPosition()
                );
            }
        }
    }

    private void initRepos(Context context) {
        mExpenseRepo = new ExpenseRepository(context);
        mChildExpenseRepo = new ChildExpenseRepository(context);
    }

    private void deleteChild(ChildItem item, int listPosition) {
        ExpenseObject child = item.getContent();

        try {
            mChildExpenseRepo.delete(child);

            if (null != mCallback) {
                mCallback.onSuccess(item, listPosition);
            }
        } catch (CannotDeleteChildExpenseException e) {

            Log.e(TAG, "Could not delete ChildExpense " + child.getTitle(), e);
        }
    }

    private void deleteExpense(ExpenseItem item, int listPosition) {
        ExpenseObject expense = item.getContent();

        try {
            mExpenseRepo.delete(expense);

            if (null != mCallback) {
                mCallback.onSuccess(item, listPosition);
            }

        } catch (CannotDeleteExpenseException e) {

            Log.e(TAG, "Could not delete Booking " + expense.getTitle(), e);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem deletedItem, int deletedItemPosition);
    }
}
