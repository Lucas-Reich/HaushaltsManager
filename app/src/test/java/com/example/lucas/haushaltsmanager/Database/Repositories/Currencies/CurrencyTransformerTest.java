package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CurrencyTransformerTest {
    private CurrencyTransformer transformer;

    @Before
    public void setUp() {
        transformer = new CurrencyTransformer();
    }

    @Test
    public void testCursorToCurrencyWithValidCursor() {
        // Arrange
        final Currency expectedCurrency = getSimpleCurrency();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.CURRENCIES_COL_ID, expectedCurrency.getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_CREATED_AT, "");
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedCurrency.getName());
            put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, expectedCurrency.getShortName());
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedCurrency.getSymbol());
        }});

        // Act
        Currency transformedCurrency = transformer.transform(cursor);

        // Assert
        assertEquals(expectedCurrency, transformedCurrency);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void exceptionIsThrownIfNotEnoughColumnsArePresent() {
        // Arrange
        final Currency expectedCurrency = getSimpleCurrency();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.CURRENCIES_COL_ID, expectedCurrency.getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_CREATED_AT, "");
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedCurrency.getName());
            // Value for short name is not set
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedCurrency.getSymbol());
        }});

        // Act
        transformer.transform(cursor);
    }

    private Cursor createCursor(Map<String, Object> values) {
        String[] columns = values.keySet().toArray(new String[0]);
        MatrixCursor cursor = new MatrixCursor(columns);

        MatrixCursor.RowBuilder builder = cursor.newRow();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }

        cursor.moveToFirst();
        return cursor;
    }

    private Currency getSimpleCurrency() {
        return new Currency(
            "Euro",
            "EUR",
            "€"
        );
    }
}
