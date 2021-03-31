package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.PreferencesHelper.RebuildStrategyInterface;
import com.example.lucas.haushaltsmanager.PreferencesHelper.SharedPreferences;

import java.util.List;

public class ActiveAccountsPreferencesRebuildStrategy implements RebuildStrategyInterface {
    @Override
    public void rebuild(Context context, SharedPreferences preferences) {
        List<Account> accounts = getAccountsList(context);

        addAccountsToPreferences(context, accounts);
    }

    private List<Account> getAccountsList(Context context) {
        return new AccountRepository(context).getAll();
    }

    private void addAccountsToPreferences(Context context, List<Account> accounts) {
        ActiveAccountsPreferences preferences = new ActiveAccountsPreferences(context);

        for (Account account : accounts) {
            preferences.addAccount(account);
        }
    }
}
