package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.TemplateBooking;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;

public class TemplateMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "templateAction";

    private final OnSuccessCallback mCallback;
    private final IActionKey mActionKey = new ActionKey(ACTION_KEY);
    private final TemplateRepository templateRepository;

    public TemplateMenuItem(OnSuccessCallback callback) {
        mCallback = callback;
        templateRepository = new TemplateRepository(app.getContext());
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_template_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_template_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload actionPayload, Context context) {
        Booking templateExpense = extractExpenseFromPayload(actionPayload);

        saveAsTemplate(new TemplateBooking(templateExpense));
    }

    private Booking extractExpenseFromPayload(ActionPayload actionPayload) {
        return (Booking) actionPayload.getFirstItem().getContent();
    }

    private void saveAsTemplate(TemplateBooking templateBooking) {
        try {
            templateRepository.insert(templateBooking);

            if (null != mCallback) {
                mCallback.onSuccess(templateBooking);
            }
        } catch (TemplateCouldNotBeCreatedException e) {
            // TODO:  Do nothing?
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(TemplateBooking templateBooking);
    }
}
