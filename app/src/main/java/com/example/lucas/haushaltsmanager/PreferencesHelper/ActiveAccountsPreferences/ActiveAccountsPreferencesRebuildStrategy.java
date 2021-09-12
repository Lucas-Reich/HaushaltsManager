package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import android.content.Context;

import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.entities.Account;
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
        return Room.databaseBuilder(context, AppDatabase.class, "expenses")
                .build().accountDAO().getAll();
    }

    private void addAccountsToPreferences(Context context, List<Account> accounts) {
        ActiveAccountsPreferences preferences = new ActiveAccountsPreferences(context);

        for (Account account : accounts) {
            preferences.addAccount(account);
        }
    }
}
