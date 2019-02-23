package com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import java.util.ArrayList;
import java.util.List;

public class RevertExpenseDeletionSnackbar {
    private static final String TAG = RevertExpenseDeletionSnackbar.class.getSimpleName();

    private List<IRecyclerItem> mItems;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mExpenseRepo;
    private String mSnackbarMessage;
    private OnExpenseRestoredListener mBetterListener;

    public RevertExpenseDeletionSnackbar(Context context) {
        mItems = new ArrayList<>();

        mExpenseRepo = new ExpenseRepository(context);
        mChildExpenseRepo = new ChildExpenseRepository(context);

        mSnackbarMessage = "";
    }

    public void setOnExpenseRestoredListener(OnExpenseRestoredListener listener) {
        mBetterListener = listener;
    }

    public void setMessage(String message) {
        mSnackbarMessage = message;
    }

    public void showSnackbar(View parent) {
        Snackbar.make(parent, mSnackbarMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.revert_action, getOnUndoClickListener())
                .show();
    }

    public void addItem(IRecyclerItem item) {
        mItems.add(item);
    }

    private View.OnClickListener getOnUndoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (IRecyclerItem item : mItems) {
                    if (item instanceof ChildItem) {
                        restoreChildExpenses((ChildItem) item);
                    } else if (item instanceof ExpenseItem) {
                        restoreExpense((ExpenseItem) item);
                    }
                }
            }
        };
    }

    private void restoreExpense(ExpenseItem item) {
        ExpenseObject expense = item.getContent();

        Log.i(TAG, "Restoring ParentExpense " + expense.getTitle());
        ExpenseObject restoredExpense = mExpenseRepo.insert(expense);

        if (mBetterListener != null)
            mBetterListener.onExpenseRestored(new ExpenseItem(restoredExpense));
    }

    private void restoreChildExpenses(ChildItem child) {
        ExpenseObject childExpense = child.getContent();

        try {
            ExpenseObject parent = mExpenseRepo.get(child.getParentId());

            Log.i(TAG, "Restoring ChildExpense " + childExpense.getTitle() + " and attaching it to ParentExpense " + parent.getTitle());
            ExpenseObject restoredChild = mChildExpenseRepo.addChildToBooking(childExpense, parent);

            if (mBetterListener != null)
                mBetterListener.onExpenseRestored(new ChildItem(restoredChild, parent.getIndex()));

        } catch (AddChildToChildException e) {
            Log.e(TAG, "Could not restore ChildExpense " + childExpense.getTitle(), e);
        } catch (ExpenseNotFoundException e) {
            Log.e(TAG, "Could not find ParentExpense for Child" + childExpense.getTitle(), e);
        }
    }

    public interface OnExpenseRestoredListener {
        void onExpenseRestored(IRecyclerItem item);
    }
}
