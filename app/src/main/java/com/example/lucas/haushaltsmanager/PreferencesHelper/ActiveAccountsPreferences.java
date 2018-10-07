package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveAccountsPreferences {
    private static final String ACTIVE_ACCOUNTS = "ActiveAccounts";

    private SharedPreferences mPreferences;
    private AccountRepository mAccountRepo;

    public ActiveAccountsPreferences(Context context, AccountRepository accountRepository) {

        mAccountRepo = accountRepository;
        mPreferences = context.getSharedPreferences(ACTIVE_ACCOUNTS, Context.MODE_PRIVATE);
    }

    public void addAccount(Account account) {

        mPreferences.edit().putBoolean(account.getIndex() + "", true).apply();
    }

    public void removeAccount(Account account) {

        mPreferences.edit().remove(account.getIndex() + "").apply();
    }

    public void setVisibility(Account account, boolean visibility) {

        mPreferences.edit().putBoolean(account.getIndex() + "", visibility).apply();
    }

    public boolean isActive(Account account) {
        //todo sollte ich unterscheiden zwischen Konten die nicht gefunden werden konnten und Konten die es tatsächlich gibt?

        return mPreferences.getBoolean(account.getIndex() + "", false);
    }

    public List<Account> getActiveAccounts() {
        Map<String, ?> allAccounts = mPreferences.getAll();
        List<Account> activeAccounts = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allAccounts.entrySet()) {
            boolean value = (Boolean) entry.getValue();
            if (value) {

                activeAccounts.add(fetchAccount(Long.getLong(entry.getKey())));
            }
        }

        return activeAccounts;
    }

    private Account fetchAccount(long index) {

        try {

            return mAccountRepo.get(index);
        } catch (AccountNotFoundException e) {

            return null;
        }
    }
}
