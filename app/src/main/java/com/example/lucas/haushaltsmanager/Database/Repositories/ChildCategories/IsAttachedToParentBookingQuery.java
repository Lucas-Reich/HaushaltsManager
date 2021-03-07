package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;

class IsAttachedToParentBookingQuery implements QueryInterface {
    private final Category childCategory;

    public IsAttachedToParentBookingQuery(Category childCategory) {
        this.childCategory = childCategory;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childCategory.getIndex()
        };
    }
}
