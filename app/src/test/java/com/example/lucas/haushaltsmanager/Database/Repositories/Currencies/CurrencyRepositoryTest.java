package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CannotDeleteCurrencyException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class CurrencyRepositoryTest {
    private CurrencyRepository mCurrencyRepo;

    /**
     * Manager welcher die Datenbank verbindungen hält
     */
    private DatabaseManager mDatabaseManagerInstance;

    @Before
    public void setup() {
        mDatabaseManagerInstance = DatabaseManager.getInstance();
        mCurrencyRepo = new CurrencyRepository(RuntimeEnvironment.application);
    }

    @After
    public void teardown() {

        // Keine Ahnung warum das so funktioniert aber irgendwie tut es das
        // Angepasste Quelle: https://stackoverflow.com/questions/34742685/robolectric-running-multiple-tests-fails
        mCurrencyRepo.closeDatabase();
        mDatabaseManagerInstance.closeDatabase();
    }

    private Currency getSimpleCurrency() {
        return new Currency(
                "Euro",
                "EUR",
                "€"
        );
    }

    @Test
    public void testExistsWithExistingCurrency() {
        Currency currency = mCurrencyRepo.create(getSimpleCurrency());

        boolean exists = mCurrencyRepo.exists(currency);
        assertTrue("Could not find Currency in database", exists);
    }

    @Test
    public void testExistsWithNotExistingCurrencyShouldFail() {
        Currency notExistingCurrency = getSimpleCurrency();

        boolean exists = mCurrencyRepo.exists(notExistingCurrency);
        assertFalse("Found Currency in database", exists);
    }

    @Test
    public void testGetWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = mCurrencyRepo.create(getSimpleCurrency());

        try {
            Currency fetchedCurrency = mCurrencyRepo.get(expectedCurrency.getIndex());
            assertEquals(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Could not find Currency in database");
        }
    }

    @Test
    public void testGetWithNotExistingCurrencyShouldThrowCurrencyNotFoundException() {
        long notExistingCurrencyId = 123;

        try {
            mCurrencyRepo.get(notExistingCurrencyId);
            Assert.fail("Not existing Currency was found");

        } catch (CurrencyNotFoundException e) {

            assertEquals(String.format("Could not find Currency with id %s.", notExistingCurrencyId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidCurrencyShouldSucceed() {
        Currency expectedCurrency = mCurrencyRepo.create(getSimpleCurrency());

        try {
            Currency fetchedCurrency = mCurrencyRepo.get(expectedCurrency.getIndex());
            assertEquals(expectedCurrency, fetchedCurrency);
            assertTrue("Could not find Currency in database", mCurrencyRepo.exists(expectedCurrency));

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Could not find just created Currency");
        }
    }

    @Test
    public void testDeleteWithExistingCurrencyShouldSucceed() {
        Currency currency = mCurrencyRepo.create(getSimpleCurrency());

        try {
            mCurrencyRepo.delete(currency);
            assertFalse("Currency was not deleted", mCurrencyRepo.exists(currency));

        } catch (CannotDeleteCurrencyException e) {

            Assert.fail("Could not delete Currency");
        }
    }

    @Test
    public void testDeleteWithNotExistingCurrencyShouldSucceed() {
        Currency currency = getSimpleCurrency();

        try {
            mCurrencyRepo.delete(currency);
            assertFalse("Found Currency in database", mCurrencyRepo.exists(currency));

        } catch (CannotDeleteCurrencyException e) {

            Assert.fail("Not existing Currency could be deleted");
        }
    }

    @Test
    public void testDeleteWithExistingCurrencyAttachedToAccountShouldFailThrowCannotDeleteCurrencyException() {
        Currency currency = mCurrencyRepo.create(getSimpleCurrency());

        AccountRepository mockAccountRepo = mock(AccountRepository.class);
        when(mockAccountRepo.isCurrencyAttachedToAccount(currency)).thenReturn(true);

        injectMock(mCurrencyRepo, mockAccountRepo, "mAccountRepo");

        try {
            mCurrencyRepo.delete(currency);
            Assert.fail("Could delete Currency assigned to Account");

        } catch (CannotDeleteCurrencyException e) {

            assertTrue("Could not find Currency in database", mCurrencyRepo.exists(currency));
            assertEquals(String.format("Currency %s cannot be deleted.", currency.getName()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = mCurrencyRepo.create(getSimpleCurrency());

        try {
            expectedCurrency.setName("New Name");
            mCurrencyRepo.update(expectedCurrency);
            Currency fetchedCurrency = mCurrencyRepo.get(expectedCurrency.getIndex());

            assertEquals(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Could not find just created Currency");
        }
    }

    @Test
    public void testUpdateWithNotExistingCurrencyShouldThrowCurrencyNotFoundException() {
        Currency currency = getSimpleCurrency();

        try {
            currency.setName("Dollar");
            mCurrencyRepo.update(currency);

            Assert.fail("Not existing Currency could be updated");

        } catch (CurrencyNotFoundException e) {

            assertEquals(String.format("Could not find Currency with id %s.", currency.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToCurrencyWithValidCursor() {
        Currency expectedCurrency = getSimpleCurrency();

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

        Currency cursorCurrency = CurrencyRepository.fromCursor(cursor);
        assertEquals(expectedCurrency, cursorCurrency);
    }

    @Test
    public void testCursorToCurrencyWithInvalidCursorThrowCursorIndexOutOfBoundsException() {
        Currency expectedCurrency = getSimpleCurrency();

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
            CurrencyRepository.fromCursor(cursor);
            Assert.fail("Could createExpenseItems Currency from incomplete Cursor");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    /**
     * Methode um ein Feld einer Klasse durch ein anderes, mit injection, auszutauschen.
     *
     * @param obj       Objekt welches angepasst werden soll
     * @param value     Neuer Wert des Felds
     * @param fieldName Name des Feldes
     */
    private void injectMock(Object obj, Object value, String fieldName) {
        try {
            Class cls = obj.getClass();

            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {

            Assert.fail(String.format("Could not find field %s in class %s", fieldName, obj.getClass().getSimpleName()));
        }
    }
}
