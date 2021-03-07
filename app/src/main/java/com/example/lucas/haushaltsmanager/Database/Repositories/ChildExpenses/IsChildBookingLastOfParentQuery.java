package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class IsChildBookingLastOfParentQuery implements QueryInterface {
    private final ExpenseObject childBooking;

    public IsChildBookingLastOfParentQuery(ExpenseObject childBooking) {
        this.childBooking = childBooking;
    }

    @Override
    public String sql() {
        String subSelect = "(SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = %s"
                + ")";

        return "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = " + subSelect
                + ";";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childBooking.getIndex()
        };
    }
}
