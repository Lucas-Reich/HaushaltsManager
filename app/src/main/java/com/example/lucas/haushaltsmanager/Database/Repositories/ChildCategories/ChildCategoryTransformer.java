package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import java.util.ArrayList;

public class ChildCategoryTransformer implements TransformerInterface<Category> {
    @Override
    public Category transform(Cursor c) {
        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;

        return new Category(
                categoryIndex,
                categoryName,
                new Color(categoryColor),
                ExpenseType.load(defaultExpenseType),
                new ArrayList<Category>()
        );
    }
}
