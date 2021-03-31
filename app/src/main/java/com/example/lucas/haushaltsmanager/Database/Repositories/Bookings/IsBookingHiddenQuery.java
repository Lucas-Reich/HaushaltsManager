package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class IsBookingHiddenQuery implements QueryInterface {
    private final ExpenseObject booking;

    public IsBookingHiddenQuery(ExpenseObject booking) {
        this.booking = booking;
    }

    @Override
    public String sql() {
        return "SELECT hidden FROM BOOKINGS WHERE id = '%s';";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                booking.getId().toString()
        };
    }
}
