package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.content.Intent;

import com.example.lucas.haushaltsmanager.Activities.ExpenseScreen;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;

import androidx.annotation.DrawableRes;

public class AddChildMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "addChildAction";
    private IActionKey mActionKey;

    public AddChildMenuItem() {
        mActionKey = new ActionKey("addChildAction");
    }

    @Override
    @DrawableRes
    public int getIconRes() {
        return R.drawable.ic_add_child_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    public int getHintRes() {
        return R.string.fab_menu_item_add_child_hin;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload payload, Context context) {
        ExpenseObject parent = (ExpenseObject) payload.getFirstItem().getContent();

        context.startActivity(createIntent(parent, context));
    }

    private Intent createIntent(ExpenseObject expense, Context context) {
        Intent addChildIntent = new Intent(context, ExpenseScreen.class);
        addChildIntent.putExtra(ExpenseScreen.INTENT_MODE, ExpenseScreen.INTENT_MODE_ADD_CHILD);
        addChildIntent.putExtra(ExpenseScreen.INTENT_BOOKING, expense);

        return addChildIntent;
    }
}
