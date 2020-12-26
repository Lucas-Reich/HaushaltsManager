package com.example.lucas.haushaltsmanager.PreferencesHelper;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.List;

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
        Account account = getSimpleAccount(1);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.addAccount(account);

        verify(editor, times(1)).putBoolean("1", true);
        verify(editor, times(1)).apply();
    }

    @Test
    public void testRemoveAccount() {
        Account account = getSimpleAccount(1);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.removeAccount(account);

        verify(editor, times(1)).remove("1");
        verify(editor, times(1)).apply();
    }

    @Test
    public void testChangeVisibility() {
        Account account = getSimpleAccount(5);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.contains(account.getIndex() + "")).thenReturn(true);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.changeVisibility(account, false);

        verify(editor, times(1)).putBoolean("5", false);
        verify(editor, times(1)).apply();
    }

    @Test
    public void testChangeVisibilityWithNotExistingAccount() {
        Account account = getSimpleAccount(27);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.contains(account.getIndex() + "")).thenReturn(false);
        when(preferences.edit()).thenReturn(editor);

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        mPreferences.changeVisibility(account, false);

        verify(editor, times(0)).putBoolean("27", false);
        verify(editor, times(0)).apply();
    }

    @Test
    public void testGetActiveAccounts() {
        Account expectedAccount1 = getSimpleAccount(1);
        Account expectedAccount2 = getSimpleAccount(2);

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("1", true);
        map.put("2", true);

        SharedPreferences preferences = mock(SharedPreferences.class);
        doReturn(map).when(preferences).getAll();

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        List<Long> actualAccounts = mPreferences.getActiveAccounts();

        assertEquals(expectedAccount1.getIndex(), (long) actualAccounts.get(0));
        assertEquals(expectedAccount2.getIndex(), (long) actualAccounts.get(1));
    }

    @Test
    public void testGetActiveAccountsShouldRemoveEntriesWithInvalidKeyInPreferences() {
        HashMap<String, Boolean> invalidMap = new HashMap<>();
        invalidMap.put("NoLongValue", true);

        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);

        SharedPreferences preferences = mock(SharedPreferences.class);
        when(preferences.edit()).thenReturn(editor);
        doReturn(invalidMap).when(preferences).getAll();

        when(mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)).thenReturn(preferences);

        mPreferences = new ActiveAccountsPreferences(mContext);
        List<Long> actualAccounts = mPreferences.getActiveAccounts();

        assertTrue(actualAccounts.isEmpty());
        verify(editor, times(1)).remove("NoLongValue");
        verify(editor, times(1)).apply();
    }

    private Account getSimpleAccount(long id) {
        return new Account(
                id,
                "Konto",
                new Price(0d, mock(Currency.class))
        );
    }
}
