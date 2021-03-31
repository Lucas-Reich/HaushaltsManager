package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class IsChildBookingLastVisibleOfParentQuery implements QueryInterface {
    private final ExpenseObject parentBooking;

    public IsChildBookingLastVisibleOfParentQuery(ExpenseObject parentBooking) {
        this.parentBooking = parentBooking;
    }

    @Override
    public String sql() {
        return "SELECT * FROM BOOKINGS WHERE id = '%s' AND hidden != 1";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                parentBooking.getId().toString()
        };
    }
}
