package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.content.Intent;

import com.example.lucas.haushaltsmanager.Activities.EditRecurringBooking;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;

public class RecurringMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "recurringAction";

    private IActionKey mActionKey;

    public RecurringMenuItem() {
        mActionKey = new ActionKey(ACTION_KEY);
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_repeat_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_recurring_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload actionPayload, Context context) {
        Booking recurringExpense = extractExpenseFromPayload(actionPayload);

        context.startActivity(getIntent(recurringExpense, context));
    }

    private Booking extractExpenseFromPayload(ActionPayload actionPayload) {
        return (Booking) actionPayload.getFirstItem().getContent();
    }

    private Intent getIntent(Booking recurringExpense, Context context) {
        Intent recurringIntent = new Intent(context, EditRecurringBooking.class);
        recurringIntent.putExtra(EditRecurringBooking.INTENT_BOOKING, recurringExpense);

        return recurringIntent;
    }
}
