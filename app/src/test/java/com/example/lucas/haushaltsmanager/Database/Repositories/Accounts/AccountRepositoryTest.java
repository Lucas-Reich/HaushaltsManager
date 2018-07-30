package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AccountRepositoryTest {

    @Before
    public void setup() {

        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    @Test
    public void testExistsWithExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 100, currency);
        account = AccountRepository.insert(account);

        boolean exists = AccountRepository.exists(account);
        assertTrue("Das Konto konnte nicht in der Datenbank gefunden werrden", exists);
    }

    @Test
    public void testExistsWithNotExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 0, currency);

        boolean exists = AccountRepository.exists(account);
        assertFalse("Nicht existierendes Konto konnte in der Datenbank gefunden werden", exists);
    }

    @Test
    public void testGetWithExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account expectedAccount = new Account("Konto", 0, currency);
        expectedAccount = AccountRepository.insert(expectedAccount);

        try {
            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());
            assertSameAccounts(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingAccountShouldThrowAccountNotFoundException() {
        long notExistingAccountId = 1337;

        try {
            AccountRepository.get(notExistingAccountId);
            Assert.fail("Nicht existierendes Kont konnte gefunden werden");

        } catch (AccountNotFoundException e) {

            assertEquals(String.format("Could not find Account with id %s.", notExistingAccountId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account expectedAccount = new Account("Konto", 0, currency);
        expectedAccount = AccountRepository.insert(expectedAccount);

        try {
            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());
            assertSameAccounts(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }

    @Test
    public void testInsertWithInvalidAccountShouldFail() {
        //todo was soll passieren wenn das Konto nicht richtig initialisiert wurde, zb keine währung
    }

    @Test
    public void testDeleteWithWithExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 0, currency);
        account = AccountRepository.insert(account);

        try {
            AccountRepository.delete(account);
            assertFalse("Konto wurde nicht gelöscht", AccountRepository.exists(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Konto, welches keiner Buchung zugeordnet ist, konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToParentExpenseShouldFailWithCannotDeleteAccountException() {
        Category category = mock(Category.class);
        when(category.getIndex()).thenReturn(100L);

        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 100, currency);
        account = AccountRepository.insert(account);

        ExpenseObject parentExpense = new ExpenseObject("Ausgabe", 100, false, category, account);
        ExpenseRepository.insert(parentExpense);

        try {
            AccountRepository.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine ParentBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToChildExpenseShouldFailWithCannotDeleteAccountException() {
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(100L);

        Category category = mock(Category.class);
        when(category.getIndex()).thenReturn(100L);

        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 100, currency);
        account = AccountRepository.insert(account);

        ExpenseObject childExpense = new ExpenseObject("Ausgabe", 100, false, category, account);
        ChildExpenseRepository.insert(parentExpense, childExpense);

        try {
            AccountRepository.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine KindBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account("Konto", 0, currency);

        try {
            AccountRepository.delete(account);
            assertFalse("Konto wurde in der Datenbank gefunden", AccountRepository.exists(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Nicht existierendes Konto konnte nicht gelöscht werden");
        }

    }

    @Test
    public void testUpdateWithWithExistingAccountShouldSucceed() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account expectedAccount = new Account("Konto", 0, currency);
        expectedAccount = AccountRepository.insert(expectedAccount);

        try {
            expectedAccount.setName("New Account Name");
            AccountRepository.update(expectedAccount);

            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());
            assertSameAccounts(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Gerade erstelltes Konto konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingAccountShouldThrowAccountNotFoundException() {
        Currency currency = mock(Currency.class);
        when(currency.getIndex()).thenReturn(100L);

        Account account = new Account(1337, "Konto", 0, currency);

        try {
            AccountRepository.update(account);
            Assert.fail("Nicht existierendes Konto konnte geupdated werden");

        } catch (AccountNotFoundException e) {

            assertEquals(String.format("Could not find Account with id %s.", account.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToAccountWithValidCursorShouldSucceed() {
        Account expectedAccount = new Account(1337, "Konto", 0, new Currency(1, "Währung", "WÄH", "W"));

        String[] columns = new String[]{
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                ExpensesDbHelper.ACCOUNTS_COL_BALANCE,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedAccount.getIndex(), expectedAccount.getTitle(), expectedAccount.getBalance(), expectedAccount.getCurrency().getIndex(), expectedAccount.getCurrency().getName(), expectedAccount.getCurrency().getShortName(), expectedAccount.getCurrency().getSymbol()});
        cursor.moveToFirst();

        try {
            Account fetchedAccount = AccountRepository.cursorToAccount(cursor);
            assertSameAccounts(expectedAccount, fetchedAccount);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Konto konnte nicht aus einem Cursor hergestellt werden");
        }
    }

    @Test
    public void testCursorToAccountWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Account expectedAccount = new Account(1337, "Konto", 0, new Currency("Währung", "WÄH", "W"));

        String[] columns = new String[]{
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                //Der Kontostand ist nicht mit im Cursor
                ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedAccount.getIndex(), expectedAccount.getTitle(), expectedAccount.getCurrency().getIndex()});
        cursor.moveToFirst();

        try {
            Account fetchedAccount = AccountRepository.cursorToAccount(cursor);
            assertSameAccounts(expectedAccount, fetchedAccount);
            Assert.fail("Konto konnte aus einem Fehlerhaften Cursor wiederhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    private void assertSameAccounts(Account expected, Account actual) {
        assertEquals(expected, actual);
    }
}
