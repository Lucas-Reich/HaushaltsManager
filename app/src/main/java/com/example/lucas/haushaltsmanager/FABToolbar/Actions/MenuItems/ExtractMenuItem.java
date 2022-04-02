package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class ExtractMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "extractAction";

    private final IActionKey mActionKey;

    private final OnSuccessCallback mCallback;
    private final ParentBookingDAO parentBookingRepository;

    public ExtractMenuItem(OnSuccessCallback callback, Context context) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);
        parentBookingRepository = AppDatabase.getDatabase(context).parentBookingDAO();
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_extract_child_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_extract_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload actionPayload, Context context) {
        for (IRecyclerItem selectedItem : actionPayload.getItems()) {
            Booking childBooking = ((ChildExpenseItem) selectedItem).getContent();

            parentBookingRepository.extractChildFromParent(childBooking);

            if (null != mCallback) {
                mCallback.onSuccess(selectedItem, childBooking);
            }
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem extractedItem, Booking newExpense);
    }
}
