package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.PreferencesHelper.SharedPreferences;

import java.util.List;
import java.util.UUID;

public interface ActiveAccountsPreferencesInterface extends SharedPreferences {
    void addAccount(Account account);

    void removeAccount(Account account);

    void changeVisibility(Account account, boolean visibility);

    boolean isActive(Account account);

    List<UUID> getActiveAccounts();

    List<UUID> getAll();
}
