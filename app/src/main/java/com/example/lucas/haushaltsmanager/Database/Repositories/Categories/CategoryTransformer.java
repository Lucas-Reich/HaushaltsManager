package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;

import java.util.UUID;

public class CategoryTransformer implements TransformerInterface<Category> {
    @Override
    public Category transform(Cursor c) {
        String categoryName = c.getString(c.getColumnIndex("name"));
        String categoryColor = c.getString(c.getColumnIndex("color"));

        return new Category(
                getId(c),
                categoryName,
                new Color(categoryColor),
                getExpenseType(c)
        );
    }

    private UUID getId(Cursor c) {
        int columnIndex = c.getColumnIndex("category_id");
        if (-1 == columnIndex) {
            columnIndex = c.getColumnIndex("id");
        }

        String rawId = c.getString(columnIndex);

        return UUID.fromString(rawId);
    }

    private ExpenseType getExpenseType(Cursor c) {
        boolean rawExpenseType = c.getInt(c.getColumnIndex("default_expense_type")) == 1;

        return ExpenseType.load(rawExpenseType);
    }
}
