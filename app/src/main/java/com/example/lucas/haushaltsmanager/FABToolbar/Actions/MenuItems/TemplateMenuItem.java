package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;

public class TemplateMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "templateAction";

    private IActionKey mActionKey;

    private OnSuccessCallback mCallback;

    public TemplateMenuItem(OnSuccessCallback callback) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);
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
        ExpenseObject templateExpense = extractExpenseFromPayload(actionPayload);

        saveAsTemplate(templateExpense, context);
    }

    private ExpenseObject extractExpenseFromPayload(ActionPayload actionPayload) {
        return (ExpenseObject) actionPayload.getFirstItem().getContent();
    }

    private void saveAsTemplate(ExpenseObject templateExpense, Context context) {
        TemplateRepository templateRepo = new TemplateRepository(context);
        Template template = templateRepo.insert(new Template(templateExpense));

        if (null != mCallback) {
            mCallback.onSuccess(template);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(Template template);
    }
}
