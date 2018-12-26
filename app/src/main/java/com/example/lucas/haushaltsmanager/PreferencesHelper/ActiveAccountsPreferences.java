package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveAccountsPreferences {
    private static final String TAG = ActiveAccountsPreferences.class.getSimpleName();

    private static final String PREFERENCES_NAME = "ActiveAccounts";
    private static boolean DEFAULT_ACCOUNT_STATUS = true;

    private SharedPreferences mPreferences;
    private AccountRepository mAccountRepo;

    public ActiveAccountsPreferences(Context context, AccountRepository accountRepository) {

        mAccountRepo = accountRepository;
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void addAccount(Account account) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(stringify(account.getIndex()), DEFAULT_ACCOUNT_STATUS);
        editor.apply();
    }

    public void removeAccount(Account account) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(stringify(account.getIndex()));
        editor.apply();
    }

    public void changeVisibility(Account account, boolean visibility) {
        if (mPreferences.contains(stringify(account.getIndex()))) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(stringify(account.getIndex()), visibility);
            editor.apply();
        }

    }

    public boolean isActive(Account account) {

        return mPreferences.getBoolean(stringify(account.getIndex()), false);
    }

    public List<Account> getActiveAccounts() {
        Map<String, ?> allAccounts = mPreferences.getAll();

        return getAccounts(allAccounts);
    }

    private String stringify(long value) {
        return value + "";
    }

    private List<Account> getAccounts(Map<String, ?> accounts) {

        List<Account> activeAccounts = new ArrayList<>();
        for (Map.Entry<String, ?> entry : accounts.entrySet()) {
            if (isAccountActive(entry)) {
                Account account = entryToAccount(entry);
                if (null != account) {
                    activeAccounts.add(entryToAccount(entry));
                }
            }
        }

        return activeAccounts;
    }

    private boolean isAccountActive(Map.Entry<String, ?> entry) {
        return (Boolean) entry.getValue();
    }

    private Account entryToAccount(Map.Entry<String, ?> entry) {

        try {
            long accountId = Long.parseLong(entry.getKey());

            return mAccountRepo.get(accountId);
        } catch (AccountNotFoundException e) {
            Log.e(TAG, String.format("Failed to retrieve account %s from database", entry.getKey()), e);

            forceRemoveEntry(entry);

            return null;
        } catch (NumberFormatException e) {
            Log.e(TAG, String.format("Failed to convert '%s' to long.", entry.getKey()), e);

            forceRemoveEntry(entry);

            return null;
        }
    }

    private void forceRemoveEntry(Map.Entry<String, ?> entry) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(entry.getKey());
        editor.apply();
    }
}
