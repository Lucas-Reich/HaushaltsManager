package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BookingTransformerTest {
    private BookingTransformer transformer;

    @Before
    public void setUp() {
        transformer = new BookingTransformer(
                new CurrencyTransformer(),
                new ChildCategoryTransformer(),
                new MockChildExpenseRepository()
        );
    }

    @Test
    public void testCursorToExpenseWithValidCursorShouldSucceed() {
        // Arrange
        final ExpenseObject expectedExpense = getSimpleExpense();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.BOOKINGS_COL_ID, expectedExpense.getIndex());
            put(ExpensesDbHelper.BOOKINGS_COL_DATE, expectedExpense.getDate().getTimeInMillis());
            put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expectedExpense.getTitle());
            put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expectedExpense.getUnsignedPrice());
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expectedExpense.isExpenditure() ? 1 : 0);
            put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expectedExpense.getNotice());
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expectedExpense.getExpenseType().name());
            put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expectedExpense.getAccountId());
            put(ExpensesDbHelper.CURRENCIES_COL_ID, expectedExpense.getCurrency().getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedExpense.getCurrency().getName());
            put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, expectedExpense.getCurrency().getShortName());
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedExpense.getCurrency().getSymbol());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID, expectedExpense.getCategory().getIndex());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, expectedExpense.getCategory().getTitle());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, expectedExpense.getCategory().getColor().getColorString());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
        }});

        // Act
        ExpenseObject actualExpense = transformer.transform(cursor);

        // Assert
        Assert.assertEquals(expectedExpense, actualExpense);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToExpenseWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        // Arrange
        final ExpenseObject expectedExpense = getSimpleExpense();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.BOOKINGS_COL_ID, expectedExpense.getIndex());
            put(ExpensesDbHelper.BOOKINGS_COL_DATE, expectedExpense.getDate().getTimeInMillis());
            // Booking title information is not present in Cursors
            put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expectedExpense.getUnsignedPrice());
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expectedExpense.isExpenditure() ? 1 : 0);
            put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expectedExpense.getNotice());
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expectedExpense.getExpenseType().name());
            put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expectedExpense.getAccountId());
            put(ExpensesDbHelper.CURRENCIES_COL_ID, expectedExpense.getCurrency().getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedExpense.getCurrency().getName());
            put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, expectedExpense.getCurrency().getShortName());
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedExpense.getCurrency().getSymbol());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID, expectedExpense.getCategory().getIndex());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, expectedExpense.getCategory().getTitle());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, expectedExpense.getCategory().getColor().getColorString());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
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

    private ExpenseObject getSimpleExpense() {
        Currency currency = getSimpleCurrency();

        return new ExpenseObject(
                "Ausgabe",
                new Price(500, true, currency),
                getSimpleCategory(),
                -1,
                currency
        );
    }

    private Category getSimpleCategory() {
        return new Category(
                -1,
                "Category name",
                Color.black(),
                ExpenseType.expense(),
                new ArrayList<Category>()
        );
    }

    private Currency getSimpleCurrency() {
        return new Currency(
                "Euro",
                "EUR",
                "€"
        );
    }
}
