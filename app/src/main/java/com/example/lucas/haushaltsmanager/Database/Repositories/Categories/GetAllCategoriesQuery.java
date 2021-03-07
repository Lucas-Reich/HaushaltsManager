package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllCategoriesQuery implements QueryInterface {

    @Override
    public String sql() {
        return "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " "
                + "FROM " + ExpensesDbHelper.TABLE_CATEGORIES + ";";
    }

    @Override
    public Object[] values() {
        return new Object[] {};
    }
}
