package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;

class HasCategoryChildrenQuery implements QueryInterface {
    private final Category parentCategory;

    public HasCategoryChildrenQuery(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    @Override
    public String sql() {
        return "SELECT *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID + " = %s"
                + ";";
    }

    @Override
    public Object[] values() {
        return new Object[] {
                parentCategory.getIndex()
        };
    }
}
