package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetChildCategoryQuery implements QueryInterface {
    private final long childCategoryId;

    public GetChildCategoryQuery(long childCategoryId) {
        this.childCategoryId = childCategoryId;
    }

    @Override
    public String sql() {
        return "SELECT "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = %s;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childCategoryId
        };
    }
}
