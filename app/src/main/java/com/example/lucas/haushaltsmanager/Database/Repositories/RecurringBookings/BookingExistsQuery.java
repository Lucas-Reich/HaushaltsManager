package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class BookingExistsQuery implements QueryInterface {
    private final ExpenseObject booking;

    public BookingExistsQuery(ExpenseObject booking) {
        this.booking = booking;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                booking.getIndex()
        };
    }
}
