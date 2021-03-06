package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

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

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class ChildExpenseTransformerTest {
    private ChildExpenseTransformer transformer;

    public void setUp() {
        transformer = new ChildExpenseTransformer(
                new CurrencyTransformer(),
                new ChildCategoryTransformer()
        );
    }

    @Test
    public void testCursorToChildBookingWithValidCursorShouldSucceed() {
        // Arrange
        final ExpenseObject expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.BOOKINGS_COL_ID, expectedChildExpense.getIndex());
            put(ExpensesDbHelper.BOOKINGS_COL_DATE, expectedChildExpense.getDate().getTimeInMillis());
            put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expectedChildExpense.getTitle());
            put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expectedChildExpense.getUnsignedPrice());
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expectedChildExpense.isExpenditure() ? 1 : 0);
            put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expectedChildExpense.getNotice());
            put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expectedChildExpense.getAccountId());
            put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expectedChildExpense.getCurrency().getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedChildExpense.getCurrency().getName());
            put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, expectedChildExpense.getCurrency().getShortName());
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedChildExpense.getCurrency().getSymbol());
            put(ExpensesDbHelper.CATEGORIES_COL_ID, expectedChildExpense.getCategory().getIndex());
            put(ExpensesDbHelper.CATEGORIES_COL_NAME, expectedChildExpense.getCategory().getTitle());
            put(ExpensesDbHelper.CATEGORIES_COL_COLOR, expectedChildExpense.getCategory().getColor().getColorString());
            put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedChildExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
        }});

        // Act
        ExpenseObject transformedChildExpense = transformer.transform(cursor);

        // Assert
        assertEquals(expectedChildExpense, transformedChildExpense);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToChildBookingWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        final ExpenseObject expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.BOOKINGS_COL_ID, expectedChildExpense.getIndex());
            put(ExpensesDbHelper.BOOKINGS_COL_DATE, expectedChildExpense.getDate().getTimeInMillis());
            put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expectedChildExpense.getTitle());
            // Price is not present in the result set
            put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expectedChildExpense.isExpenditure() ? 1 : 0);
            put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expectedChildExpense.getNotice());
            put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expectedChildExpense.getAccountId());
            put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expectedChildExpense.getCurrency().getIndex());
            put(ExpensesDbHelper.CURRENCIES_COL_NAME, expectedChildExpense.getCurrency().getName());
            put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, expectedChildExpense.getCurrency().getShortName());
            put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, expectedChildExpense.getCurrency().getSymbol());
            put(ExpensesDbHelper.CATEGORIES_COL_ID, expectedChildExpense.getCategory().getIndex());
            put(ExpensesDbHelper.CATEGORIES_COL_NAME, expectedChildExpense.getCategory().getTitle());
            put(ExpensesDbHelper.CATEGORIES_COL_COLOR, expectedChildExpense.getCategory().getColor().getColorString());
            put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedChildExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
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
        Category category = new Category("Kategorie", new Color("#121212"), ExpenseType.expense(), new ArrayList<Category>());
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(3135, false, currency),
                category,
                -1,
                currency
        );
    }
}
