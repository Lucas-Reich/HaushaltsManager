package com.example.lucas.haushaltsmanager.PreferencesHelper;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ActiveAccountsPreferencesTest {
    private static final String PREFERENCES_NAME = "ActiveAccounts";

    private ActiveAccountsPreferences mPreferences;
    private Context mContext;

    @Before
    public void setup() {
        mContext = mock(Context.class);
    }

    @After
    public void teardown() {
        mContext = null;
        mPreferences = null;
    }

    @Test
    public void testAddAccount() {
        Account account = getSimpleAccount();

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.addAccount(account);

        verify(editor, times(1)).putBoolean(account.getId().toString(), true);
        verify(editor, times(1)).apply();
    }

    @Test
    public void testRemoveAccount() {
        Account account = getSimpleAccount();

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.removeAccount(account);

        verify(editor, times(1)).remove(account.getId().toString());
        verify(editor, times(1)).apply();
    }

    @Test
    public void testChangeVisibility() {
        Account account = getSimpleAccount();

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.contains(account.getId().toString())).thenReturn(true);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.changeVisibility(account, false);

        verify(editor, times(1)).putBoolean(account.getId().toString(), false);
        verify(editor, times(1)).apply();
    }

    @Test
    public void testChangeVisibilityWithNotExistingAccount() {
        Account account = getSimpleAccount();

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.contains(account.getId().toString())).thenReturn(false);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.changeVisibility(account, false);

        verify(editor, times(0)).putBoolean(account.getId().toString(), false);
        verify(editor, times(0)).apply();
    }

    @Test
    public void testGetActiveAccounts() {
        Account expectedAccount1 = getSimpleAccount();
        Account expectedAccount2 = getSimpleAccount();

        HashMap<String, Boolean> map = new HashMap<>();
        map.put(expectedAccount1.getId().toString(), true);
        map.put(expectedAccount2.getId().toString(), true);

        SharedPreferences preferences = mock(SharedPreferences.class);
        doReturn(map).when(preferences).getAll();

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        List<UUID> actualAccounts = mPreferences.getAll();

        assertEquals(expectedAccount1.getId(), actualAccounts.get(0));
        assertEquals(expectedAccount2.getId(), actualAccounts.get(1));
    }

    @Test
    public void testGetActiveAccountsShouldRemoveEntriesWithInvalidKeyInPreferences() {
        HashMap<String, Boolean> invalidMap = new HashMap<>();
        invalidMap.put("NotExistingAccountId", true);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);
        doReturn(invalidMap).when(preferences).getAll();

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        List<UUID> actualAccounts = mPreferences.getAll();

        assertTrue(actualAccounts.isEmpty());
        verify(editor, times(1)).remove("NotExistingAccountId");
        verify(editor, times(1)).apply();
    }

    private Account getSimpleAccount() {
        return new Account(
                "Konto",
                new Price(0d)
        );
    }
}
