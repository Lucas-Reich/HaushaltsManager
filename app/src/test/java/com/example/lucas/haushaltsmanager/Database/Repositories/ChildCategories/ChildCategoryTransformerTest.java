package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class ChildCategoryTransformerTest {
    private ChildCategoryTransformer transformer;

    @Before
    public void setUp() {
        transformer = new ChildCategoryTransformer();
    }

    @Test
    public void testCursorToChildCategoryWithValidCursorShouldSucceed() {
        // Act
        final Category expectedChildCategory = getSimpleCategory();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID, expectedChildCategory.getIndex());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, expectedChildCategory.getTitle());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, expectedChildCategory.getColor().getColorString());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedChildCategory.getDefaultExpenseType().value() ? 1 : 0);
        }});

        // Act
        Category actualChildCategory = transformer.transform(cursor);

        // Assert
        assertEquals(expectedChildCategory, actualChildCategory);

    }

    @Test(expected = CursorIndexOutOfBoundsException.class)
    public void testCursorToChildCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        // Arrange
        final Category expectedChildCategory = getSimpleCategory();

        Cursor cursor = createCursor(new HashMap<String, Object>() {{
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID, expectedChildCategory.getIndex());
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, expectedChildCategory.getTitle());
            // Color information is not saved in cursor
            put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, expectedChildCategory.getDefaultExpenseType().value() ? 1 : 0);
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
            ExpenseType.expense(),
            new ArrayList<Category>()
        );
    }
}
