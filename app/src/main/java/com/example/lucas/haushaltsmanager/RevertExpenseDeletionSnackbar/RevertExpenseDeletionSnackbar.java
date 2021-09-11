package com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RevertExpenseDeletionSnackbar {
    private static final String TAG = RevertExpenseDeletionSnackbar.class.getSimpleName();

    private final List<IRecyclerItem> mItems;
    private final ChildExpenseRepository mChildExpenseRepo;
    private final ExpenseRepository mExpenseRepo;
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
                    if (item instanceof ChildExpenseItem) {
                        restoreChildExpenses((ChildExpenseItem) item);
                    } else if (item instanceof ExpenseItem) {
                        restoreExpense((ExpenseItem) item);
                    }
                }
            }
        };
    }

    private void restoreExpense(ExpenseItem item) {
        Booking expense = item.getContent();

        Log.i(TAG, "Restoring ParentExpense " + expense.getTitle());
        mExpenseRepo.insert(expense);

        if (mBetterListener != null) {
            mBetterListener.onExpenseRestored(new ExpenseItem(expense, item.getParent()));
        }
    }

    private void restoreChildExpenses(ChildExpenseItem child) {
        Booking childExpense = child.getContent();
        ParentBookingItem parentExpense = (ParentBookingItem) child.getParent();

        try {
            ParentBooking parent = parentExpense.getContent();

            Log.i(TAG, "Restoring ChildExpense " + childExpense.getTitle() + " and attaching it to ParentExpense " + parent.getTitle());
            Booking restoredChild = mChildExpenseRepo.addChildToBooking(childExpense, parent);

            if (mBetterListener != null) {
                mBetterListener.onExpenseRestored(new ChildExpenseItem(restoredChild, child.getParent()));
            }

        } catch (AddChildToChildException e) {
            Log.e(TAG, "Could not restore ChildExpense " + childExpense.getTitle(), e);
        }
    }

    public interface OnExpenseRestoredListener {
        void onExpenseRestored(IRecyclerItem item);
    }
}
