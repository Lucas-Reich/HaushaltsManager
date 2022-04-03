package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ChildBookingItem.ChildExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.IBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;

import java.util.ArrayList;
import java.util.List;

public class CombineMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "combineBookingsAction";

    private final IActionKey mActionKey;

    private final ParentBookingDAO parentBookingDAO;
    private final ParentBookingRepository parentBookingRepository;
    private final BookingDAO bookingRepository;
    private final OnSuccessCallback mCallback;

    public CombineMenuItem(OnSuccessCallback callback, Context context) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);

        parentBookingDAO = AppDatabase.getDatabase(context).parentBookingDAO();
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO();

        parentBookingRepository = new ParentBookingRepository(parentBookingDAO, bookingRepository);
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_merge_bookings_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_combine_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload payload, Context context) {
        Bundle bundle = new Bundle();
        bundle.putString(BasicTextInputDialog.TITLE, context.getString(R.string.input_title));

        BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
        textInputDialog.setArguments(bundle);
        textInputDialog.setOnTextInputListener(getOnTextInputListener(payload.getItems()));
        textInputDialog.show(getFragmentManager(context), "");
    }

    private FragmentManager getFragmentManager(Context context) {
        ParentActivity activity = (ParentActivity) context;

        return activity.getSupportFragmentManager();
    }

    private BasicTextInputDialog.OnTextInput getOnTextInputListener(final List<IRecyclerItem> selectedItems) {
        return combinedExpenseTitle -> {
            List<IRecyclerItem> removedItems = new ArrayList<>();

            ParentBooking parent = new ParentBooking(combinedExpenseTitle);

            for (IRecyclerItem deletedChildItem : selectedItems) {
                IRecyclerItem deletedChild = deleteItem(deletedChildItem);

                if (null != deletedChild) {
                    removedItems.add(deletedChild);
                    parent.addChild((Booking) deletedChild.getContent());
                }
            }

            parentBookingDAO.insert(parent, parent.getChildren());

            if (null != mCallback) {
                mCallback.onSuccess(parent, removedItems);
            }
        };
    }

    private IBookingItem deleteItem(IRecyclerItem item) {
        if (item instanceof ExpenseItem) {
            bookingRepository.delete(((ExpenseItem) item).getContent());

            return (IBookingItem) item;
        }

        if (item instanceof ChildExpenseItem) {
            parentBookingRepository.deleteChildBooking(((ChildExpenseItem) item).getContent());

            return (IBookingItem) item;
        }

        return null;
    }

    public interface OnSuccessCallback {
        void onSuccess(ParentBooking combinedExpense, List<IRecyclerItem> removedItems);
    }
}
