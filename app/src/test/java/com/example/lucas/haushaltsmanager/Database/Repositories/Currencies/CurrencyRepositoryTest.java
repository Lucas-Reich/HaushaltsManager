package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void testGetWithExistingCurrencyShouldSucceed() {
        Currency expectedCurrency = mCurrencyRepo.insert(getSimpleCurrency());

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
        Currency expectedCurrency = mCurrencyRepo.insert(getSimpleCurrency());

        try {
            Currency fetchedCurrency = mCurrencyRepo.get(expectedCurrency.getIndex());
            assertEquals(expectedCurrency, fetchedCurrency);

        } catch (CurrencyNotFoundException e) {

            Assert.fail("Could not find just created Currency");
        }
    }

    private Currency getSimpleCurrency() {
        return new Currency(
                "Euro",
                "EUR",
                "€"
        );
    }
}
