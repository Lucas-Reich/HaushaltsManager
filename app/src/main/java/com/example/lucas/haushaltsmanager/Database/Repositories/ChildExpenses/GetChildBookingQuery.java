package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import java.util.UUID;

class GetChildBookingQuery implements QueryInterface {
    private final UUID id;

    public GetChildBookingQuery(UUID id) {
        this.id = id;
    }

    @Override
    public String sql() {
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
                + "WHERE BOOKINGS.id = '%s' "
                + "ORDER BY BOOKINGS.date DESC;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                id.toString()
        };
    }
}
