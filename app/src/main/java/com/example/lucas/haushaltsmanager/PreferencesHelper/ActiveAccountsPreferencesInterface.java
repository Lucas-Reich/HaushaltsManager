package com.example.lucas.haushaltsmanager.PreferencesHelper;

import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.List;

public interface ActiveAccountsPreferencesInterface {
    void addAccount(Account account);

    void removeAccount(Account account);

    void changeVisibility(Account account, boolean visibility);

    public boolean isActive(Account account);

    List<Long> getActiveAccounts();
}
