package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Booking.ExpenseType;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class CategoryTransformerTest {
    private CategoryTransformer transformer;

    @Before
    public void setUp() {
        transformer = new CategoryTransformer();
    }

    @Test
    public void testCursorToCategoryWithValidCursorShouldSucceed() {
        // Arrange
        final Category expectedCategory = getSimpleCategory();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("id", expectedCategory.getId().toString());
            put("name", expectedCategory.getName());
            put("color", expectedCategory.getColor().getColorString());
            put("default_expense_type", expectedCategory.getDefaultExpenseType().value() ? 1 : 0);
        }});

        // Act
        Category fetchedCategory = transformer.transform(cursor);

        // Assert
        assertEquals(expectedCategory, fetchedCategory);
    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        // Arrange
        final Category expectedCategory = getSimpleCategory();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put("id", expectedCategory.getId().toString());
            put("name", expectedCategory.getName());
            // No color information in Cursor
            put("default_expense_type", expectedCategory.getDefaultExpenseType().value() ? 1 : 0);
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

    private Category getSimpleCategory() {
        return new Category(
                "Category Name",
                Color.black(),
                ExpenseType.deposit()
        );
    }
}
