package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey;

public class ActionKey implements IActionKey {
    private String mActionKey;

    public ActionKey(String actionKey) {
        mActionKey = actionKey;
    }

    public String getActionKey() {
        return mActionKey;
    }
}
