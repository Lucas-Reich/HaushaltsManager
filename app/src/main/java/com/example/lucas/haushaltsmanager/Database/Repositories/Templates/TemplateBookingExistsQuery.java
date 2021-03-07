package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class TemplateBookingExistsQuery implements QueryInterface {
    private final ExpenseObject booking;

    public TemplateBookingExistsQuery(ExpenseObject booking) {
        this.booking = booking;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                booking.getIndex()
        };
    }
}
