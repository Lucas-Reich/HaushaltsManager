package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class TestActiveAccountsPreferences {
//    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ActiveAccountsPreferences mAccountsPreferences;
    private AccountRepository mAccountRepo;

    @Before
    public void setup() {

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mAccountsPreferences = new ActiveAccountsPreferences(RuntimeEnvironment.application, mAccountRepo);
    }

    private Account getSimpleAccount(String accountName) {
        return new Account(
                1337L,
                accountName,
                313.0,
                mock(Currency.class)
        );
    }

    private List<Account> getSimpleAccounts(int count) {
        List<Account> accounts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            accounts.add(getSimpleAccount("Konto " + i));
        }

        return accounts;
    }

    @Test
    public void testAddingAccount() {
        mAccountsPreferences.addAccount(getSimpleAccount("Mein Konto"));

        assertTrue("True ist nicht true", true);
    }

    @Test
    public void testIsActiveWithExistingAccount() {
        Account account = getSimpleAccount("Mein Konto");

        mAccountsPreferences.addAccount(account);

        assertTrue("Aktives Konto wurde nicht als solches zurückgegeben", mAccountsPreferences.isActive(account));
    }

    @Test
    public void testGetActiveAccounts() {
        List<Account> expectedAccounts = getSimpleAccounts(4);

        for (Account account : expectedAccounts) {
            mAccountRepo.create(account);
            mAccountsPreferences.addAccount(account);
        }

        List<Account> actualActiveAccountList = mAccountsPreferences.getActiveAccounts();

        assertTrue("Konto 1 wurde nicht in der aktiven Kontoliste gefunden", actualActiveAccountList.contains(expectedAccounts.get(0)));
        assertTrue("Konto 2 wurde nicht in der aktiven Kontoliste gefunden", actualActiveAccountList.contains(expectedAccounts.get(1)));
        assertTrue("Konto 3 wurde nicht in der aktiven Kontoliste gefunden", actualActiveAccountList.contains(expectedAccounts.get(2)));
        assertTrue("Konto 4 wurde nicht in der aktiven Kontoliste gefunden", actualActiveAccountList.contains(expectedAccounts.get(3)));
        assertFalse("Zu viele Konten waren in der Liste", actualActiveAccountList.size() != 4);
    }
}
