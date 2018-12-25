package com.example.lucas.haushaltsmanager.PreferencesHelper;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import junit.framework.Assert;

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
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ActiveAccountsPreferencesTest {
    // TODO: Implement missing tests
    private ActiveAccountsPreferences mPreferences;
    private AccountRepository mAccountRepo;
    private Context mContext;

    @Before
    public void setup() {
        mAccountRepo = mock(AccountRepository.class);
        mContext = mock(Context.class);
    }

    @After
    public void teardown() {
        mContext = null;
        mAccountRepo = null;
        mPreferences = null;
    }

    @Test
    public void testGetActiveAccounts() {
        Account expectedAccount1 = getSimpleAccount(1, "Konto 1");
        Account expectedAccount2 = getSimpleAccount(2, "Konto 2");

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("1", true);
        map.put("2", true);


        try {
            when(mAccountRepo.get(expectedAccount1.getIndex())).thenReturn(expectedAccount1);
            when(mAccountRepo.get(expectedAccount2.getIndex())).thenReturn(expectedAccount2);
            SharedPreferences preferences = mock(SharedPreferences.class);
            doReturn(map).when(preferences).getAll();
            when(mContext.getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE)).thenReturn(preferences);
            mPreferences = new ActiveAccountsPreferences(mContext, mAccountRepo);

            List<Account> actualAccounts = mPreferences.getActiveAccounts();

            assertEquals(expectedAccount1, actualAccounts.get(0));
            assertEquals(expectedAccount2, actualAccounts.get(1));
        } catch (AccountNotFoundException e) {

            Assert.fail();
        }
    }

    @Test
    public void testGetActiveAccountsShouldIgnoreEntriesWithInvalidKeyInPreferences() {
        HashMap<String, Boolean> invalidMap = new HashMap<>();
        invalidMap.put("NoLongValue", true);


        SharedPreferences preferences = mock(SharedPreferences.class);
        doReturn(invalidMap).when(preferences).getAll();
        when(mContext.getSharedPreferences("ActiveAccounts", Context.MODE_PRIVATE)).thenReturn(preferences);
        mPreferences = new ActiveAccountsPreferences(mContext, mAccountRepo);

        List<Account> actualAccounts = mPreferences.getActiveAccounts();

        assertTrue(actualAccounts.isEmpty());
    }

    public void testAddAccount() {

    }

    public void testRemoveAccount() {

    }

    public void testRemoveAccountWithNotExistingAccount() {

    }

    public void testChangeVisibility() {

    }

    public void testChangeVisibilityWithNotExistingAccount() {

    }

    public void testIsActive() {

    }

    public void testIsActiveWithNotExistingAccount() {

    }

    private Account getSimpleAccount(long id, String accountName) {
        return new Account(
                id,
                accountName,
                0d,
                mock(Currency.class)
        );
    }
}
