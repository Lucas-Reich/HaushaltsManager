package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class AccountRepositoryTest {
    private AccountRepositoryInterface mAccountRepo;

    private ChildExpenseRepository mChildExpenseRepo;

    /**
     * Manager welcher die Datenbank verbindungen hält
     */
    private DatabaseManager mDatabaseManagerInstance;

    @Before
    public void setup() {
        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mDatabaseManagerInstance = DatabaseManager.getInstance();

        mChildExpenseRepo = new ChildExpenseRepository(RuntimeEnvironment.application);
    }

    @After
    public void teardown() {

        mDatabaseManagerInstance.closeDatabase();
    }

    @Test
    public void testGetWithExistingAccountShouldSucceed() throws AccountCouldNotBeCreatedException {
        Account expectedAccount = getSimpleAccount();
        mAccountRepo.insert(expectedAccount);

        try {
            Account fetchedAccount = mAccountRepo.get(expectedAccount.getId());
            assertEquals(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingAccountShouldThrowAccountNotFoundException() {
        UUID notExistingAccountId = UUID.randomUUID();

        try {
            mAccountRepo.get(notExistingAccountId);
            Assert.fail("Nicht existierendes Kont konnte gefunden werden");

        } catch (AccountNotFoundException e) {

            assertEquals(String.format("Could not find Account with id %s.", notExistingAccountId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidAccountShouldSucceed() throws AccountCouldNotBeCreatedException {
        Account expectedAccount = getSimpleAccount();
        mAccountRepo.insert(expectedAccount);

        try {
            Account fetchedAccount = mAccountRepo.get(expectedAccount.getId());
            assertEquals(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }

    @Test
    public void testDeleteWithWithExistingAccountShouldSucceed() throws AccountCouldNotBeCreatedException {
        Account account = getSimpleAccount();
        mAccountRepo.insert(account);

        try {
            mAccountRepo.delete(account);
            assertFalse(accountExistsInDb(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Konto, welches keiner Buchung zugeordnet ist, konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToParentExpenseShouldFailWithCannotDeleteAccountException() throws AccountCouldNotBeCreatedException {
        Account account = getSimpleAccount();
        mAccountRepo.insert(account);

        try {
            mAccountRepo.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine ParentBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertTrue("Konto wurde gelöscht", accountExistsInDb(account));
            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToChildExpenseShouldFailWithCannotDeleteAccountException() throws AccountCouldNotBeCreatedException {
        Account account = getSimpleAccount();
        mAccountRepo.insert(account);

        ExpenseObject mockParentExpense = mock(ExpenseObject.class);

        ExpenseObject childExpense = getSimpleExpense();
        mChildExpenseRepo.insert(mockParentExpense, childExpense);

        try {
            mAccountRepo.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine KindBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertTrue("Konto wurde gelöscht", accountExistsInDb(account));
            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingAccountShouldSucceed() {
        Account account = getSimpleAccount();

        try {
            mAccountRepo.delete(account);
            assertFalse(accountExistsInDb(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Nicht existierendes Konto konnte nicht gelöscht werden");
        }

    }

    @Test
    public void testUpdateWithWithExistingAccountShouldSucceed() throws AccountCouldNotBeCreatedException {
        Account expectedAccount = getSimpleAccount();
        mAccountRepo.insert(expectedAccount);

        try {
            expectedAccount.setName("New Account Name");
            mAccountRepo.update(expectedAccount);
            Account fetchedAccount = mAccountRepo.get(expectedAccount.getId());

            assertEquals(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Gerade erstelltes Konto konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingAccountShouldThrowAccountNotFoundException() throws AccountCouldNotBeCreatedException {
        Account account = getSimpleAccount();
        mAccountRepo.insert(account);

        try {
            mAccountRepo.update(account);
            Assert.fail("Nicht existierendes Konto konnte geupdated werden");

        } catch (AccountNotFoundException e) {

            assertEquals(String.format("Could not find Account with id %s.", account.getId()), e.getMessage());
        }
    }

    private boolean accountExistsInDb(Account account) {
        List<Account> accounts = mAccountRepo.getAll();

        for (Account account1 : accounts) {
            if (account1.getId().equals(account.getId())) {
                return true;
            }
        }

        return false;
    }

    private Account getSimpleAccount() {
        return new Account(
                "Konto",
                new Price(7653)
        );
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "Ich bin eine Ausgabe",
                new Price(0),
                mock(Category.class),
                getSimpleAccount().getId()
        );
    }
}
