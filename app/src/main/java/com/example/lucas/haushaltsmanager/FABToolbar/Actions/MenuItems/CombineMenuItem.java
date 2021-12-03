package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.Activities.MainTab.ParentActivity;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
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

    private ParentBookingDAO parentBookingRepository;
    private BookingDAO bookingDao;
    private final OnSuccessCallback mCallback;

    public CombineMenuItem(OnSuccessCallback callback) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);
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
        initRepos(context);

        Bundle bundle = new Bundle();
        bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.input_title));

        BasicTextInputDialog textInputDialog = new BasicTextInputDialog();
        textInputDialog.setArguments(bundle);
        textInputDialog.setOnTextInputListener(getOnTextInputListener(payload.getItems()));
        textInputDialog.show(getFragmentManager(context), "");
    }

    private void initRepos(Context context) {
        bookingDao = AppDatabase.getDatabase(context).bookingDAO();
        parentBookingRepository = AppDatabase.getDatabase(context).parentBookingDAO();
    }

    private FragmentManager getFragmentManager(Context context) {
        ParentActivity activity = (ParentActivity) context;

        return activity.getFragmentManager();
    }

    private String getString(@StringRes int stringRes) {
        return app.getContext().getString(stringRes);
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

            parentBookingRepository.insert(parent);

            if (null != mCallback) {
                mCallback.onSuccess(parent, removedItems);
            }
        };
    }

    private IBookingItem deleteItem(IRecyclerItem item) {
        if (item instanceof ExpenseItem) {
            bookingDao.delete(((ExpenseItem) item).getContent());

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
