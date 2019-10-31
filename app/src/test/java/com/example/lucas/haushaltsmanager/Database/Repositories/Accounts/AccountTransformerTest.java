package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.Common.QueryResult;
import com.example.lucas.haushaltsmanager.Database.Exceptions.IllegalCursorException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class AccountTransformerTest {
    private AccountTransformer transformer = new AccountTransformer();

    @Test
    public void transformValidCursor() {
        Cursor c = createValidCursor();

        Account account = transformer.transform(new QueryResult(c));

        assertAccountEqualsCursor(account, c);
    }

    @Test
    public void transformWithMoreThanRequiredFieldsShouldNotFail() {
        Cursor c = createValidCursorWithAdditionalField();

        Account account = transformer.transform(new QueryResult(c));

        assertAccountEqualsCursor(account, c);
    }

    @Test
    public void transformAfterLastEntryShouldReturnNull() {
        Cursor c = createValidCursor();
        c.moveToNext();

        Account account = transformer.transform(new QueryResult(c));

        assertNull(account);
    }

    @Test
    public void transformInvalidCursor() {
        Cursor c = createInvalidCursor();

        try {
            transformer.transform(new QueryResult(c));
            Assert.fail("Invalid Cursor could be transformed!");

        } catch (IllegalCursorException e) {

            assertEquals("Could not find required field 'balance' in Cursor.", e.getMessage());
        }
    }

    @Test
    public void transformShouldNotCloseCursor() {
        Cursor c = createValidCursor();

        Account account = transformer.transform(new QueryResult(c));

        assertNotNull(account);
        assertFalse(c.isClosed());

    }

    @Test
    public void transformAndCloseShouldCloseCursor() {
        Cursor c = createValidCursor();

        Account account = transformer.transformAndClose(new QueryResult(c));

        assertNotNull(account);
        assertTrue(c.isClosed());
    }

    private void assertAccountEqualsCursor(Account account, Cursor c) {
        assertEquals(c.getLong(0), account.getIndex());
        assertEquals(c.getString(1), account.getTitle());
        assertEquals(c.getDouble(2), account.getBalance().getSignedValue());
        assertEquals(c.getLong(3), account.getBalance().getCurrency().getIndex());
        assertEquals(c.getString(4), account.getBalance().getCurrency().getName());
        assertEquals(c.getString(5), account.getBalance().getCurrency().getShortName());
        assertEquals(c.getString(6), account.getBalance().getCurrency().getSymbol());
    }

    private Cursor createValidCursor() {
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
        cursor.addRow(new Object[]{-1L, "Mein Geiles Konto", 5000.50, -1L, "Euro", "EUR", "€"});

        return cursor;
    }

    private Cursor createInvalidCursor() {
        String[] columns = new String[]{
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                // ACCOUNTS_COL_BALANCE is missing
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{-1L, "Mein Geiles Konto", -1L, "Euro", "EUR", "€"});

        return cursor;
    }

    private Cursor createValidCursorWithAdditionalField() {
        String[] columns = new String[]{
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                ExpensesDbHelper.ACCOUNTS_COL_BALANCE,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL,
                "This_field_is_ignored"
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{-1L, "Mein Geiles Konto", 5000.50, -1L, "Euro", "EUR", "€", "this_value_is_ignored"});

        return cursor;
    }
}
