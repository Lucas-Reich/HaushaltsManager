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

    public void changeVisibility(Account account, boolean visibility) {
        //TODO sollte ich die Funktion so abändern dass nur konten die auch wirklich existieren angepasst werden können?

        mPreferences.edit().putBoolean(account.getIndex() + "", visibility).apply();
    }

    public boolean isActive(Account account) {
        // TODO sollte ich unterscheiden zwischen Konten die nicht gefunden werden konnten und Konten die es tatsächlich gibt?

        return mPreferences.getBoolean(account.getIndex() + "", false);
    }

    public List<Account> getActiveAccounts() {
        Map<String, ?> allAccounts = mPreferences.getAll();

        return getAccounts(allAccounts);
    }

    private List<Account> getAccounts(Map<String, ?> accounts) {

        List<Account> activeAccounts = new ArrayList<>();
        for (Map.Entry<String, ?> entry : accounts.entrySet()) {
            // TODO: Sollte ich eine Konto welches nicht in der Datenbank gefunden werden konnte einfach aus der liste der aktiven Konten löschen?
            // oder kann es dann sein dass dieses konto immer und immer wieder in die preferences geschrieben wird?
            if (isAccountActive(entry)) {
                Account account = entryToAccount(entry);
                if (account != null) {
                    activeAccounts.add(entryToAccount(entry));
                }
            }
        }

        return activeAccounts;
    }

    private long extractIdSafe(Map.Entry<String, ?> entry) {
        try {

            return Long.parseLong(entry.getKey());
        } catch (NumberFormatException e) {
            Log.e(TAG, String.format("Failed to convert '%s' to long.", entry.getKey()), e);

            return -2;
        }
    }

    private boolean isAccountActive(Map.Entry<String, ?> entry) {
        return (Boolean) entry.getValue();
    }

    private Account entryToAccount(Map.Entry<String, ?> entry) {

        try {
            long accountId = extractIdSafe(entry);

            return mAccountRepo.get(accountId);
        } catch (AccountNotFoundException e) {
            // TODO: Wenn ein Konto nicht gefunden werden kann, dann kann man nicht mal mehr den TabOne öffnen.
            Log.e(TAG, String.format("Failed to retrieve account %s from database", entry.getKey()), e);

            return null;
        }
    }
}
