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
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
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
    private Currency currency;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);

        currency = new Currency("Euro", "EUR", "€");
        currency = CurrencyRepository.insert(currency);
    }

    public Account getSimpleAccount() {
        return new Account(
                "Konto",
                7653,
                currency
        );
    }

    @Test
    public void testExistsWithExistingAccountShouldSucceed() {
        Account account = AccountRepository.insert(getSimpleAccount());

        boolean exists = AccountRepository.exists(account);
        assertTrue("Das Konto konnte nicht in der Datenbank gefunden werrden", exists);
    }

    @Test
    public void testExistsWithNotExistingAccountShouldSucceed() {
        Account account = getSimpleAccount();

        boolean exists = AccountRepository.exists(account);
        assertFalse("Nicht existierendes Konto konnte in der Datenbank gefunden werden", exists);
    }

    @Test
    public void testGetWithExistingAccountShouldSucceed() {
        Account expectedAccount = AccountRepository.insert(getSimpleAccount());

        try {
            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());
            assertEquals(expectedAccount, fetchedAccount);

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
        Account expectedAccount = AccountRepository.insert(getSimpleAccount());

        try {
            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());
            assertEquals(expectedAccount, fetchedAccount);

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
        Account account = AccountRepository.insert(getSimpleAccount());

        try {
            AccountRepository.delete(account);
            assertFalse("Konto wurde nicht gelöscht", AccountRepository.exists(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Konto, welches keiner Buchung zugeordnet ist, konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToParentExpenseShouldFailWithCannotDeleteAccountException() {
        Account account = AccountRepository.insert(getSimpleAccount());

        Category category = mock(Category.class);
        when(category.getIndex()).thenReturn(100L);

        ExpenseObject parentExpense = new ExpenseObject("Ausgabe", 0, false, category, account.getIndex(), currency);
        ExpenseRepository.insert(parentExpense);

        try {
            AccountRepository.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine ParentBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertTrue("Konto wurde gelöscht", AccountRepository.exists(account));
            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingAccountAttachedToChildExpenseShouldFailWithCannotDeleteAccountException() {
        Account account = AccountRepository.insert(getSimpleAccount());

        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(100L);

        Category category = mock(Category.class);
        when(category.getIndex()).thenReturn(100L);

        ExpenseObject childExpense = new ExpenseObject("Ausgabe", 0, false, category, account.getIndex(), currency);
        ChildExpenseRepository.insert(parentExpense, childExpense);

        try {
            AccountRepository.delete(account);
            Assert.fail("Konto konnte gelöscht werden obwohl es eine KindBuchung mit diesem Konto gibt");

        } catch (CannotDeleteAccountException e) {

            assertTrue("Konto wurde gelöscht", AccountRepository.exists(account));
            assertEquals(String.format("Account %s cannot be deleted.", account.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingAccountShouldSucceed() {
        Account account = getSimpleAccount();

        try {
            AccountRepository.delete(account);
            assertFalse("Konto wurde in der Datenbank gefunden", AccountRepository.exists(account));

        } catch (CannotDeleteAccountException e) {

            Assert.fail("Nicht existierendes Konto konnte nicht gelöscht werden");
        }

    }

    @Test
    public void testUpdateWithWithExistingAccountShouldSucceed() {
        Account expectedAccount = AccountRepository.insert(getSimpleAccount());

        try {
            expectedAccount.setName("New Account Name");
            AccountRepository.update(expectedAccount);
            Account fetchedAccount = AccountRepository.get(expectedAccount.getIndex());

            assertEquals(expectedAccount, fetchedAccount);

        } catch (AccountNotFoundException e) {

            Assert.fail("Gerade erstelltes Konto konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingAccountShouldThrowAccountNotFoundException() {
        Account account = getSimpleAccount();

        try {
            AccountRepository.update(account);
            Assert.fail("Nicht existierendes Konto konnte geupdated werden");

        } catch (AccountNotFoundException e) {

            assertEquals(String.format("Could not find Account with id %s.", account.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToAccountWithValidCursorShouldSucceed() {
        Account expectedAccount = getSimpleAccount();

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
            assertEquals(expectedAccount, fetchedAccount);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Konto konnte nicht aus einem Cursor hergestellt werden");
        }
    }

    @Test
    public void testCursorToAccountWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Account expectedAccount = getSimpleAccount();

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
            AccountRepository.cursorToAccount(cursor);
            Assert.fail("Konto konnte aus einem Fehlerhaften Cursor wiederhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }
}
