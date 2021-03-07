package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class HasBookingChildrenQuery implements QueryInterface {
    private final ExpenseObject booking;

    public HasBookingChildrenQuery(ExpenseObject booking) {
        this.booking = booking;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                booking.getIndex()
        };
    }
}
