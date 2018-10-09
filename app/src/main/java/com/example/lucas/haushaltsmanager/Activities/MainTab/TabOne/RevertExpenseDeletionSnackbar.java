package com.example.lucas.haushaltsmanager.Activities.MainTab.TabOne;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpListViewSelectedItem;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RevertExpenseDeletionSnackbar {
    private static final String TAG = RevertExpenseDeletionSnackbar.class.getSimpleName();

    private HashMap<ExpenseObject, List<ExpenseObject>> mExpenses;
    private ChildExpenseRepository mChildExpenseRepo;
    private OnRestoredActionListener mListener;
    private ExpenseRepository mExpenseRepo;
    private String mSnackbarMessage;

    RevertExpenseDeletionSnackbar(Context context) {
        mExpenses = new HashMap<>();

        mExpenseRepo = new ExpenseRepository(context);
        mChildExpenseRepo = new ChildExpenseRepository(context);

        mSnackbarMessage = "";
    }

    void setOnRestoredActionListener(OnRestoredActionListener listener) {
        mListener = listener;
    }

    void setMessage(String message) {
        mSnackbarMessage = message;
    }

    void showSnackbar(View parent) {
        Snackbar.make(parent, mSnackbarMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.revert_action, getOnUndoClickListener())
                .show();
    }

    void addItem(final ExpListViewSelectedItem item) {
        if (null == item.getParent()) {
            if (!mExpenses.containsKey(item.getItem()))
                mExpenses.put(item.getItem(), new ArrayList<ExpenseObject>());
        } else {
            if (!mExpenses.containsKey(item.getParent())) {
                mExpenses.put(item.getParent(), new ArrayList<ExpenseObject>() {{
                    add(item.getItem());
                }});
            } else {
                List<ExpenseObject> children = mExpenses.get(item.getParent());
                children.add(item.getItem());
                mExpenses.put(item.getParent(), children);
            }
        }
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
        Log.i(TAG, "Restoring ParentExpense " + expense.getKey().getTitle());
        mExpenseRepo.insert(mapEntryToExpense(expense));
    }

    private void restoreChildExpenses(ExpenseObject parent, List<ExpenseObject> children) {
        for (ExpenseObject child : children) {
            try {
                Log.i(TAG, "Restoring ChildExpense " + child.getTitle() + " and attaching it to ParentExpense " + parent.getTitle());
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
