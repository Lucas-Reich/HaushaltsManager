package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.PreferencesHelper.SharedPreferences;

import java.util.List;

public interface ActiveAccountsPreferencesInterface extends SharedPreferences {
    void addAccount(Account account);

    void removeAccount(Account account);

    void changeVisibility(Account account, boolean visibility);

    boolean isActive(Account account);

    List<Long> getActiveAccounts();
}
