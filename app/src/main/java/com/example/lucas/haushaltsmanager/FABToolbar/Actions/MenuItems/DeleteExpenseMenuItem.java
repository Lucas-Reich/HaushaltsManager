package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class DeleteExpenseMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "deleteAction";

    private final IActionKey mActionKey;

    private final OnSuccessCallback mCallback;
    private final BookingDAO bookingRepository;
    private final ParentBookingDAO parentBookingRepository;

    public DeleteExpenseMenuItem(OnSuccessCallback callback, Context context) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);

        bookingRepository = AppDatabase.getDatabase(context).bookingDAO();
        parentBookingRepository = AppDatabase.getDatabase(context).parentBookingDAO();
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

    private void deleteChild(ChildExpenseItem item) {
        Booking child = item.getContent();

        parentBookingRepository.deleteChildBooking(child);

        if (null != mCallback) {
            mCallback.onSuccess(item);
        }
    }

    private void deleteExpense(ExpenseItem item) {
        Booking expense = item.getContent();

        bookingRepository.delete(expense);

        if (null != mCallback) {
            mCallback.onSuccess(item);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem deletedItem);
    }
}
