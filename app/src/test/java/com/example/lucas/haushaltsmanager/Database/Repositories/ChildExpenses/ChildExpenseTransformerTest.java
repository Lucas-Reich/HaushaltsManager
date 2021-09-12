package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Price;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class ChildExpenseTransformerTest {
    private ChildExpenseTransformer transformer;

    public void setUp() {
        transformer = new ChildExpenseTransformer(
                new CategoryTransformer()
        );
    }

    @Test
    public void testCursorToChildBookingWithValidCursorShouldSucceed() {
        // Arrange
        final Booking expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(Booking.EXPENSE_TYPES.CHILD_EXPENSE);

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("BOOKINGS.id", expectedChildExpense.getId().toString());
            put("BOOKINGS.date", expectedChildExpense.getDate().getTimeInMillis());
            put("BOOKINGS.title", expectedChildExpense.getTitle());
            put("BOOKINGS.price", expectedChildExpense.getUnsignedPrice());
            put("BOOKINGS.expenditure", expectedChildExpense.isExpenditure() ? 1 : 0);
            put("BOOKINGS.notice", expectedChildExpense.getNotice());
            put("BOOKINGS.account_id", expectedChildExpense.getAccountId());
            put("CATEGORIES.id", expectedChildExpense.getCategory().getId().toString());
            put("CATEGORIES.name", expectedChildExpense.getCategory().getName());
            put("CATEGORIES.color", expectedChildExpense.getCategory().getColor().getColorString());
            put("CATEGORIES.default_expense_type", expectedChildExpense.getCategory().getDefaultExpenseType().getType() ? 1 : 0);
        }});

        // Act
        Booking transformedChildExpense = transformer.transform(cursor);

        // Assert
        assertEquals(expectedChildExpense, transformedChildExpense);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToChildBookingWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        final Booking expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(Booking.EXPENSE_TYPES.CHILD_EXPENSE);

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("BOOKINGS.id", expectedChildExpense.getId().toString());
            put("BOOKINGS.date", expectedChildExpense.getDate().getTimeInMillis());
            put("BOOKINGS.title", expectedChildExpense.getTitle());
            // Price is not present in Cursor
            put("BOOKINGS.expenditure", expectedChildExpense.isExpenditure() ? 1 : 0);
            put("BOOKINGS.notice", expectedChildExpense.getNotice());
            put("BOOKINGS.account_id", expectedChildExpense.getAccountId());
            put("CATEGORIES.id", expectedChildExpense.getCategory().getId().toString());
            put("CATEGORIES.name", expectedChildExpense.getCategory().getName());
            put("CATEGORIES.color", expectedChildExpense.getCategory().getColor().getColorString());
            put("CATEGORIES.default_expense_type", expectedChildExpense.getCategory().getDefaultExpenseType().getType() ? 1 : 0);
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

    private Booking getSimpleExpense() {
        Category category = new Category("Kategorie", new Color("#121212"), ExpenseType.Companion.expense());

        return new Booking(
                "Ausgabe",
                new Price(3135, false),
                category,
                UUID.randomUUID()
        );
    }
}
