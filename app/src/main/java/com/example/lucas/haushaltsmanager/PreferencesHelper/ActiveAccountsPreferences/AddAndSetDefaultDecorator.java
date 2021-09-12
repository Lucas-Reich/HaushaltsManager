package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import android.content.Context;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;

import java.util.List;
import java.util.UUID;

public class AddAndSetDefaultDecorator implements ActiveAccountsPreferencesInterface {
    private final ActiveAccountsPreferencesInterface preferences;
    private final UserSettingsPreferences userPreferences;

    public AddAndSetDefaultDecorator(ActiveAccountsPreferencesInterface preferences, Context context) {
        this.preferences = preferences;
        userPreferences = new UserSettingsPreferences(context);
    }

    @Override
    public void addAccount(Account account) {
        preferences.addAccount(account);

        userPreferences.setActiveAccount(account);
    }

    @Override
    public void removeAccount(Account account) {
        preferences.removeAccount(account);
    }

    @Override
    public void changeVisibility(Account account, boolean visibility) {
        preferences.changeVisibility(account, visibility);
    }

    @Override
    public boolean isActive(Account account) {
        return preferences.isActive(account);
    }

    @Override
    public List<UUID> getActiveAccounts() {
        return preferences.getActiveAccounts();
    }

    @Override
    public List<UUID> getAll() {
        return preferences.getAll();
    }

    @Override
    public void clear() {
        preferences.clear();
    }
}
