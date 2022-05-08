package com.example.lucas.haushaltsmanager.RevertExpenseDeletionSnackbar;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RevertExpenseDeletionSnackbar {
    private static final String TAG = RevertExpenseDeletionSnackbar.class.getSimpleName();

    private final List<IRecyclerItem> mItems;
    private final BookingDAO bookingRepository;
    private final ParentBookingDAO parentBookingRepository;
    private String mSnackbarMessage = "";
    private OnExpenseRestoredListener mBetterListener;

    public RevertExpenseDeletionSnackbar(Context context) {
        mItems = new ArrayList<>();

        bookingRepository = AppDatabase.getDatabase(context).bookingDAO();
        parentBookingRepository = AppDatabase.getDatabase(context).parentBookingDAO();
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
        return v -> {
            for (IRecyclerItem item : mItems) {
                if (item instanceof ChildExpenseItem) {
                    restoreChildExpenses((ChildExpenseItem) item);
                } else if (item instanceof ExpenseItem) {
                    restoreExpense((ExpenseItem) item);
                }
            }
        };
    }

    private void restoreExpense(ExpenseItem item) {
        Booking expense = item.getContent();

        Log.i(TAG, "Restoring ParentExpense " + expense.getTitle());
        bookingRepository.insert(expense);

        if (mBetterListener != null) {
            mBetterListener.onExpenseRestored(new ExpenseItem(expense, item.getParent()));
        }
    }

    private void restoreChildExpenses(ChildExpenseItem child) {
        Booking childBooking = child.getContent();

        ParentBookingItem parentBookingItem = (ParentBookingItem) child.getParent();
        ParentBooking parentBooking = parentBookingItem.getContent();

        Log.i(TAG, "Restoring ChildBooking " + childBooking.getTitle() + " and attaching it to ParentBooking " + parentBooking.getTitle());
        parentBookingRepository.insert(parentBooking, Collections.singletonList(childBooking));

        if (mBetterListener != null) {
            mBetterListener.onExpenseRestored(new ChildExpenseItem(childBooking, parentBookingItem));
        }
    }

    public interface OnExpenseRestoredListener {
        void onExpenseRestored(IRecyclerItem item);
    }
}
