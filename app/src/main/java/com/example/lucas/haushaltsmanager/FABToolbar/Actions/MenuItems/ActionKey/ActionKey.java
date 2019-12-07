package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

public class ActionKey implements IActionKey {
    private String mActionKey;

    public ActionKey(String actionKey) {
        mActionKey = actionKey;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ActionKey)) {
            return false;
        }

        ActionKey other = (ActionKey) obj;

        return other.toString().equals(toString());
    }

    @NonNull
    @Override
    public String toString() {
        return mActionKey;
    }
}
