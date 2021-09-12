package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;

class ChildBookingExistsQuery implements QueryInterface {
    private final Booking childBooking;

    public ChildBookingExistsQuery(Booking childBooking) {
        this.childBooking = childBooking;
    }

    @Override
    public String sql() {
        return "SELECT * "
                + "FROM BOOKINGS "
                + "WHERE id = %s "
                + "AND title = '%s' "
                + "AND price = %s "
                + "AND expense_type = '%s' "
                + "AND category_id = %s "
                + "AND account_id = %s "
                + "AND expenditure = %s "
                + "AND date = '%s' "
                + "AND notice = '%s' "
                + "AND parent_id IS NOT NULL "
                + "LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childBooking.getId().toString(),
                childBooking.getTitle(),
                childBooking.getUnsignedPrice(),
                childBooking.getExpenseType().name(),
                childBooking.getCategory().getId().toString(),
                childBooking.getAccountId().toString(),
                (childBooking.isExpenditure() ? 1 : 0),
                childBooking.getDate().getTimeInMillis(),
                childBooking.getNotice()
        };
    }
}
