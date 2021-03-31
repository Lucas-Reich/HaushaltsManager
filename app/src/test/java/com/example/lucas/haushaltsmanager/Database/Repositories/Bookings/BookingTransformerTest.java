package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BookingTransformerTest {
    private BookingTransformer transformer;

    @Before
    public void setUp() {
        transformer = new BookingTransformer(
                new CategoryTransformer()
        );
    }

    @Test
    public void testCursorToExpenseWithValidCursorShouldSucceed() {
        // Arrange
        final ExpenseObject expectedExpense = getSimpleExpense();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("BOOKINGS.id", expectedExpense.getId().toString());
            put("BOOKINGS.date", expectedExpense.getDate().getTimeInMillis());
            put("BOOKINGS.title", expectedExpense.getTitle());
            put("BOOKINGS.price", expectedExpense.getUnsignedPrice());
            put("BOOKINGS.expenditure", expectedExpense.isExpenditure() ? 1 : 0);
            put("BOOKINGS.notice", expectedExpense.getNotice());
            put("BOOKINGS.expense_type", expectedExpense.getExpenseType().name());
            put("BOOKINGS.account_id", expectedExpense.getAccountId());
            put("CATEGORIES.id", expectedExpense.getCategory().getId().toString());
            put("CATEGORIES.name", expectedExpense.getCategory().getTitle());
            put("CATEGORIES.color", expectedExpense.getCategory().getColor().getColorString());
            put("CATEGORIES.default_expense_type", expectedExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
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
            put("BOOKINGS.id", expectedExpense.getId().toString());
            put("BOOKINGS.date", expectedExpense.getDate().getTimeInMillis());
            // Booking title is not present
            put("BOOKINGS.price", expectedExpense.getUnsignedPrice());
            put("BOOKINGS.expenditure", expectedExpense.isExpenditure() ? 1 : 0);
            put("BOOKINGS.notice", expectedExpense.getNotice());
            put("BOOKINGS.expense_type", expectedExpense.getExpenseType().name());
            put("BOOKINGS.account_id", expectedExpense.getAccountId());
            put("CATEGORIES.id", expectedExpense.getCategory().getId().toString());
            put("CATEGORIES.name", expectedExpense.getCategory().getTitle());
            put("CATEGORIES.color", expectedExpense.getCategory().getColor().getColorString());
            put("CATEGORIES.default_expense_type", expectedExpense.getCategory().getDefaultExpenseType().value() ? 1 : 0);
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
        return new ExpenseObject(
                "Ausgabe",
                new Price(500, true),
                getSimpleCategory(),
                UUID.randomUUID()
        );
    }

    private Category getSimpleCategory() {
        return new Category(
                "Category name",
                Color.black(),
                ExpenseType.expense()
        );
    }
}
