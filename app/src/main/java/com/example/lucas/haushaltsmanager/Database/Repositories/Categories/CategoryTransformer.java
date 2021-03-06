package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

public class CategoryTransformer implements TransformerInterface<Category> {
    private final ChildCategoryRepositoryInterface childCategoryRepository;

    public CategoryTransformer(ChildCategoryRepositoryInterface childCategoryRepository) {
        this.childCategoryRepository = childCategoryRepository;
    }

    @Override
    public Category transform(Cursor c) {
        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;

        return new Category(
            categoryIndex,
            categoryName,
            new Color(categoryColor),
            ExpenseType.load(defaultExpenseType),
            childCategoryRepository.getAll(categoryIndex)
        );
    }
}
