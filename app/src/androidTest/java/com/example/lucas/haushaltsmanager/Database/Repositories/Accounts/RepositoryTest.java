package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.lucas.haushaltsmanager.Database.DatabaseTest;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Fixtures.AccountFixtures;
import com.example.lucas.haushaltsmanager.Fixtures.ExpenseWithExistingAccountFixtures;
import com.example.lucas.haushaltsmanager.Fixtures.IFixtures;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class RepositoryTest extends DatabaseTest {
    private Repository repository;

    public RepositoryTest() {
        repository = new Repository(getDefaultDatabase());
    }

    @Override
    public Context getContext() {
        return InstrumentationRegistry.getContext();
    }

    @Before
    public void before() {
        List<IFixtures> fixtures = new ArrayList<>();
        fixtures.add(new AccountFixtures());
        fixtures.add(new ExpenseWithExistingAccountFixtures());

        super.insertFixtures(fixtures);
    }

    @After
    public void after() {
        super.clearTables();
    }

    @Test
    public void existsShouldFindExistingAccount() {
        boolean exists = repository.exists(3);

        assertTrue(exists);
    }

    @Test
    public void existsShouldNotFindNotExistingAccount() {
        boolean exists = repository.exists(ExpensesDbHelper.INVALID_INDEX);

        assertFalse(exists);
    }

    @Test
    public void findShouldReturnAccountIfExisting() {
        Account account = repository.find(2);

        assertNotNull(account);
        assertEquals(account.getIndex(), 2L);
        assertEquals(account.getTitle(), "Konto 2");
        assertEquals(account.getBalance().getSignedValue(), 313.1D);
        assertEquals(account.getBalance().getCurrency().getIndex(), 1L);
    }

    @Test
    public void findShouldReturnNullForNotExistingAccount() {
        Account account = repository.find(ExpensesDbHelper.INVALID_INDEX);

        assertNull(account);
    }

    @Test
    public void getShouldReturnAccountForId() {
        try {

            Account account = repository.get(1L);

            assertNotNull(account);
            assertEquals(account.getIndex(), 1L);
            assertEquals(account.getTitle(), "Konto 1");
            assertEquals(account.getBalance().getSignedValue(), 0);
            assertEquals(account.getBalance().getCurrency().getIndex(), 1L);
        } catch (AccountNotFoundException e) {

            Assert.fail("Could not get existing Account.");
        }
    }

    @Test
    public void getShouldThrowExceptionForNotExistingAccount() {
        try {
            repository.get(777);

            Assert.fail("Could get not existing Account.");
        } catch (AccountNotFoundException e) {

            assertEquals("Could not find Account with id 777.", e.getMessage());
        }
    }

    @Test
    public void deleteShouldSucceed() {
        boolean accountDeleted = repository.delete(3);

        assertTrue(accountDeleted);
        assertFalse(repository.exists(3));
    }

    @Test
    public void deleteShouldSucceedForNotExistingAccount() {
        assertFalse(repository.exists(77));

        boolean accountDeleted = repository.delete(77);

        assertTrue(accountDeleted);
    }

    @Test
    public void deleteShouldFailIfAccountIsAttachedToBooking() {
        boolean accountDeleted = repository.delete(4);

        assertFalse(accountDeleted);
    }

    @Test
    public void updateShouldSucceed() {
        Account updatedAccount = new Account(2, "Neuer Konto Name", 313.1, getDummyCurrency());

        boolean accountUpdated = repository.update(updatedAccount);
        Account actualAccount = repository.find(updatedAccount.getIndex());

        assertTrue(accountUpdated);
        assertEquals(updatedAccount, actualAccount);
    }

    @Test
    public void updateShouldReturnFailsForNotExistingAccount() {
        Account notExistingAccount = new Account(ExpensesDbHelper.INVALID_INDEX, "Any String", 0D, getDummyCurrency());

        boolean accountUpdated = repository.update(notExistingAccount);

        assertFalse(accountUpdated);

    }

    @Test
    public void saveShouldInsertAccountAndReturnItWithSetId() {
        Account account = new Account("Meine geiles Konto", 0, getDummyCurrency());

        Account insertedAccount = repository.save(account);

        assertTrue(repository.exists(insertedAccount.getIndex()));
        assertEquals(4, insertedAccount.getIndex());
    }

    private Currency getDummyCurrency() {
        return new Currency(
                "Euro",
                "EUR",
                "€"
        );
    }
}
