package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class IsChildBookingLastVisibleOfParentQuery implements QueryInterface {
    private final ExpenseObject parentBooking;

    public IsChildBookingLastVisibleOfParentQuery(ExpenseObject parentBooking) {
        this.parentBooking = parentBooking;
    }

    @Override
    public String sql() {
        return "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_HIDDEN + " != 1"
                + ";";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                parentBooking.getIndex()
        };
    }
}
