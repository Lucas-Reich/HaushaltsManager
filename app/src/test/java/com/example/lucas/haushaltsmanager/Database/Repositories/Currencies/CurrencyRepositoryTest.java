package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CannotDeleteCurrencyException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class CurrencyRepositoryTest {
    //todo ich kann irgendwie nicht alle tests auf einmal ausführen weil dann alles tests ab dem dritten fehlschlagen

    @Before
    public void setup() {

        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    @Test
    public void testExistsWithExistingCurrency() {
        Currency currency = new Currency("Euro", "EUR", "€");
        currency = CurrencyRepository.insert(currency);

        boolean exists = CurrencyRepository.exists(currency);
        assertTrue("Die Währung wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingCurrencyShouldFail() {
        Currency notExistingCurrency = new Currency("Currency Name", "CSN", "C");

        boolean exists = CurrencyRepository.exists(notExistingCurrency);
        assertFalse("Die Währung wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = new Currency("Euro", "EUR", "€");
        expectedCurrency = CurrencyRepository.insert(expectedCurrency);

        try {
            Currency fetchedCurrency = CurrencyRepository.get(expectedCurrency.getIndex());
            assertSameCurrencies(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Währung wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingCurrencyShouldThrowCurrencyNotFoundException() {
        long notExistingCurrencyId = 123;

        try {
            CurrencyRepository.get(notExistingCurrencyId);
            Assert.fail("Nicht existierenden Wärhung wurde in der Datenbank gefunden");

        } catch (CurrencyNotFoundException e) {

            assertEquals(String.format("Could not find Currency with id %s.", notExistingCurrencyId), e.getMessage());
        }
    }

    @Test
    public void testGetByNameWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = new Currency("Euro", "EUR", "€");
        expectedCurrency = CurrencyRepository.insert(expectedCurrency);

        try {
            Currency fetchedCurrency = CurrencyRepository.getByShortName(expectedCurrency.getShortName());
            assertSameCurrencies(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Währung wurde nicht gefunden");
        }
    }

    @Test
    public void testGetByNameWithNotExistingCurrencyShouldThrowCurrencyNotFoundException() {
        String notExistingCurrencyShortName = "NES";

        try {
            CurrencyRepository.getByShortName(notExistingCurrencyShortName);
            Assert.fail("Nicht existierenden Wärhung wurde in der Datenbank gefunden");

        } catch (CurrencyNotFoundException e) {

            assertEquals(String.format("Could not find Currency with short name %s.", notExistingCurrencyShortName), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidCurrencyShouldSucceed() {
        Currency expectedCurrency = new Currency("Hong Kong Dollar", "HKD", "$");
        expectedCurrency = CurrencyRepository.insert(expectedCurrency);

        try {
            Currency fetchedCurrency = CurrencyRepository.get(expectedCurrency.getIndex());
            assertSameCurrencies(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Gerade erstellte Währung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testInsertWithInvalidCurrencyShouldFail() {
        //todo was sollte passieren wenn die Currency nicht richtig initialisiert wurde, zb keinen Namen o.ä.
    }

    @Test
    public void testDeleteWithExistingCurrencyShouldSucceed() {
        Currency currency = new Currency("Schweizer Franke", "CHF", "Fr.");
        currency = CurrencyRepository.insert(currency);

        try {
            CurrencyRepository.delete(currency);
            assertFalse("Währung wurde nicht gelöscht", CurrencyRepository.exists(currency));

        } catch (CannotDeleteCurrencyException e) {

            Assert.fail("Währung die zu keinem Konto zugeordnet ist konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingCurrencyShouldSucceed() {
        Currency currency = new Currency(1337, "Currency Name", "CUN", "C");

        try {
            CurrencyRepository.delete(currency);

        } catch (CannotDeleteCurrencyException e) {

            Assert.fail("Nicht existierende Währung konnte nicht gelöscht werden.");
        }
    }

    @Test
    public void testDeleteWithExistingCurrencyAttachedToAccountShouldFailThrowCannotDeleteCurrencyException() {
        Currency currency = new Currency("Euro", "EUR", "€");
        currency = CurrencyRepository.insert(currency);

        Account account = new Account("Konto 1", 100, currency);
        AccountRepository.insert(account);

        try {
            CurrencyRepository.delete(currency);
            Assert.fail("Währung konnte gelöscht werden obwohl es ein Konto mit dieser Währung gibt");

        } catch (CannotDeleteCurrencyException e) {

            assertEquals(String.format("Currency %s cannot be deleted.", currency.getName()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = new Currency("Euro", "EUR", "€");
        expectedCurrency = CurrencyRepository.insert(expectedCurrency);

        try {
            expectedCurrency.setName("New Name");
            CurrencyRepository.update(expectedCurrency);

            Currency fetchedCurrency = CurrencyRepository.get(expectedCurrency.getIndex());
            assertSameCurrencies(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Gerade erstellte Währung konnte nicht geupdated werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingCurrencyShouldThrowCurrencyNotFoundException() {
        Currency currency = new Currency(1337, "Not Existing Currency", "NEC", "N");

        try {
            CurrencyRepository.update(currency);
            Assert.fail("Nicht existierende Währung konnte geupdated werden");

        } catch (CurrencyNotFoundException e) {

            assertEquals(String.format("Could not find Currency with id %s.", currency.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToCurrencyWithValidCursor() {
        Currency expectedCurrency = new Currency(313, "Meine Currency", "CUR", "C");

        String[] columns = new String[]{
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_CREATED_AT,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedCurrency.getIndex(), "1532772073359", expectedCurrency.getName(), expectedCurrency.getShortName(), expectedCurrency.getSymbol()});
        cursor.moveToFirst();

        Currency cursorCurrency = CurrencyRepository.cursorToCurrency(cursor);
        assertSameCurrencies(expectedCurrency, cursorCurrency);
    }

    @Test
    public void testCursorToCurrencyWithInvalidCursorThrowCursorIndexOutOfBoundsException() {
        Currency expectedCurrency = new Currency(1337, "Kanadische Dollar", "CAD", "$");

        String[] columns = new String[]{
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_CREATED_AT,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                //Die Abkürzung ist nicht mit im Cursor
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedCurrency.getIndex(), "1532772073359", expectedCurrency.getName(), expectedCurrency.getSymbol()});
        cursor.moveToFirst();

        try {
            CurrencyRepository.cursorToCurrency(cursor);
            Assert.fail("Währung konnte aus einem Cursor erstellt werden, in dem Felder fehlen");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    private void assertSameCurrencies(Currency expected, Currency actual) {
        assertEquals(expected, actual);
    }
}
