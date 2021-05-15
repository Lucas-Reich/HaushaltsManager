package com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActiveAccountsPreferences implements ActiveAccountsPreferencesInterface {
    public static final String PREFERENCES_NAME = "ActiveAccounts.xml";
    private static final String TAG = ActiveAccountsPreferences.class.getSimpleName();
    private static final boolean DEFAULT_ACCOUNT_VISIBILITY = true;

    private final SharedPreferences mPreferences;

    public ActiveAccountsPreferences(Context context) {
        mPreferences = context.getSharedPreferences(
                PREFERENCES_NAME.split("\\.")[0],
                Context.MODE_PRIVATE
        );
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public void addAccount(Account account) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(account.getId().toString(), DEFAULT_ACCOUNT_VISIBILITY);
        editor.apply();
    }

    public void removeAccount(Account account) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(account.getId().toString());
        editor.apply();
    }

    public void changeVisibility(Account account, boolean visibility) {
        if (!mPreferences.contains(account.getId().toString())) {
            return;
        }

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(account.getId().toString(), visibility);
        editor.apply();
    }

    public boolean isActive(Account account) {
        return mPreferences.getBoolean(account.getId().toString(), false);
    }

    public List<UUID> getActiveAccounts() {
        Map<String, ?> map = mPreferences.getAll();

        List<UUID> activeAccounts = new ArrayList<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if ((Boolean) entry.getValue()) {
                activeAccounts.add(UUID.fromString(entry.getKey()));
            }
        }

        return activeAccounts;
    }

    public List<UUID> getAll() {
        Map<String, ?> map = mPreferences.getAll();

        return toUUIDList(map);
    }

    private List<UUID> toUUIDList(Map<String, ?> map) {
        List<UUID> idList = new ArrayList<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            idList.add(getId(entry));
        }

        return idList;
    }

    private UUID getId(Map.Entry<String, ?> entry) {

        try {
            return UUID.fromString(entry.getKey());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, String.format("Failed to convert '%s' to long.", entry.getKey()), e);

            forceRemoveEntry(entry);

            return app.getNilUuid();
        }
    }

    private void forceRemoveEntry(Map.Entry<String, ?> entry) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(entry.getKey());
        editor.apply();
    }
}
