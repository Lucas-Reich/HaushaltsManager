package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllBookingsQuery implements QueryInterface {
    private final long startDateInMillis;
    private final long endDateInMillis;

    public GetAllBookingsQuery(long startDateInMillis, long endDateInMillis) {
        this.startDateInMillis = startDateInMillis;
        this.endDateInMillis = endDateInMillis;
    }

    @Override
    public String sql() {
        return "SELECT "
                + "BOOKINGS.id, "
                + "BOOKINGS.expense_type, "
                + "BOOKINGS.price, "
                + "BOOKINGS.expenditure, "
                + "BOOKINGS.title, "
                + "BOOKINGS.date, "
                + "BOOKINGS.notice, "
                + "BOOKINGS.account_id, "
                + "BOOKINGS.category_id, "
                + "CATEGORIES.name, "
                + "CATEGORIES.color, "
                + "CATEGORIES.default_expense_type "
                + "FROM BOOKINGS "
                + "LEFT JOIN CATEGORIES ON BOOKINGS.category_id = CATEGORIES.id "
                + "WHERE BOOKINGS.date BETWEEN %s AND %s "
                + "AND BOOKINGS.hidden != 1 "
                + "AND BOOKINGS.parent_id IS NULL "
                + "ORDER BY BOOKINGS.date DESC;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                startDateInMillis,
                endDateInMillis
        };
    }
}
