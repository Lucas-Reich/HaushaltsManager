package com.example.lucas.haushaltsmanager.Activities.MainTab.TabOne;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RevertExpenseDeletionSnackbar {
    private static final String TAG = RevertExpenseDeletionSnackbar.class.getSimpleName();

    private HashMap<ExpenseObject, List<ExpenseObject>> mExpenses;
    private ExpenseRepository mExpenseRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private OnRestoredActionListener mListener;

    RevertExpenseDeletionSnackbar(HashMap<ExpenseObject, List<ExpenseObject>> expenses, Context context) {
        mExpenses = expenses;

        mExpenseRepo = new ExpenseRepository(context);
        mChildExpenseRepo = new ChildExpenseRepository(context);
    }

    void setOnRestoredActionListener(OnRestoredActionListener listener) {
        mListener = listener;
    }

    void showSnackbar(View parent, String snackbarMessage) {
        Snackbar.make(parent, snackbarMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.revert_action, getOnUndoClickListener())
                .show();
    }

    private View.OnClickListener getOnUndoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<ExpenseObject, List<ExpenseObject>> entry : mExpenses.entrySet()) {
                    if (mExpenseRepo.exists(entry.getKey()))
                        restoreChildExpenses(entry.getKey(), entry.getValue());
                    else
                        restoreParentExpense(entry);
                }

                if (null != mListener)
                    mListener.onExpensesRestored();
            }
        };
    }

    private void restoreParentExpense(Map.Entry<ExpenseObject, List<ExpenseObject>> expense) {
        mExpenseRepo.insert(mapEntryToExpense(expense));
    }

    private void restoreChildExpenses(ExpenseObject parent, List<ExpenseObject> children) {
        for (ExpenseObject child : children) {
            try {
                mChildExpenseRepo.addChildToBooking(child, parent);
            } catch (AddChildToChildException e) {
                Log.e(TAG, "Could not restore ChildExpense " + child.getTitle(), e);
            }
        }
    }

    private ExpenseObject mapEntryToExpense(Map.Entry<ExpenseObject, List<ExpenseObject>> entry) {
        ExpenseObject expense = entry.getKey();
        expense.removeChildren();
        expense.addChildren(entry.getValue());

        return expense;
    }

    public interface OnRestoredActionListener {
        // sollte die Methode die Anzahl der fehlgeschlagenen versuche zurückgeben?
        void onExpensesRestored();
    }
}
