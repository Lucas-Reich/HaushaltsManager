package com.example.lucas.haushaltsmanager.PreferencesHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Account.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActiveAccountsPreferences {
    private static final String TAG = ActiveAccountsPreferences.class.getSimpleName();

    private static final String PREFERENCES_NAME = "ActiveAccounts";
    private static boolean DEFAULT_ACCOUNT_STATUS = true;

    private SharedPreferences mPreferences;

    public ActiveAccountsPreferences(Context context) {

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

    public List<Long> getActiveAccounts() {
        Map<String, ?> map = mPreferences.getAll();

        return toLongList(map);
    }

    private List<Long> toLongList(Map<String, ?> map) {
        List<Long> idList = new ArrayList<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            long id = getId(entry);

            if (-1 != id)
                idList.add(id);
        }

        return idList;
    }

    private long getId(Map.Entry<String, ?> entry) {
        try {

            return Long.parseLong(entry.getKey());
        } catch (NumberFormatException e) {
            Log.e(TAG, String.format("Failed to convert '%s' to long.", entry.getKey()), e);

            forceRemoveEntry(entry);

            return -1;
        }
    }

    private String stringify(long value) {
        return value + "";
    }

    private void forceRemoveEntry(Map.Entry<String, ?> entry) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(entry.getKey());
        editor.apply();
    }
}
