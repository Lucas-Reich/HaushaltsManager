package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;

class GetParentBookingQuery implements QueryInterface {
    private final Booking childExpense;

    public GetParentBookingQuery(Booking childExpense) {
        this.childExpense = childExpense;
    }

    @Override
    public String sql() {
        String subQuery = "(SELECT"
                + " BOOKINGS.parent_id"
                + " FROM BOOKINGS"
                + " WHERE BOOKINGS.id = '%s'"
                + ")";

        return "SELECT "
                + "BOOKINGS.id, "
                + "BOOKINGS.price, "
                + "BOOKINGS.expenditure, "
                + "BOOKINGS.title, "
                + "BOOKINGS.date, "
                + "BOOKINGS.account_id, "
                + "BOOKINGS.category_id, "
                + "CATEGORIES.name, "
                + "CATEGORIES.color, "
                + "CATEGORIES.default_expense_type "
                + "FROM BOOKINGS "
                + "LEFT JOIN CATEGORIES ON BOOKINGS.category_id = CATEGORIES.id "
                + "WHERE BOOKINGS.id = " + subQuery + " "
                + "ORDER BY BOOKINGS.date DESC;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childExpense.getId().toString()
        };
    }
}
