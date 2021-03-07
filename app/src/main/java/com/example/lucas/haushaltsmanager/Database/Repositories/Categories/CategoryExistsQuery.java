package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;

class CategoryExistsQuery implements QueryInterface {
    private final Category category;

    public CategoryExistsQuery(Category category) {
        this.category = category;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[] {
                category.getIndex(),
                category.getTitle(),
                category.getColor().getColorString(),
                (category.getDefaultExpenseType().value() ? 1 : 0)
        };
    }
}
