package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetRecurringBookingQuery implements QueryInterface {
    private final long recurringBookingId;

    public GetRecurringBookingQuery(long recurringBookingId) {
        this.recurringBookingId = recurringBookingId;
    }

    @Override
    public String sql() {
        return "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = %s";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                recurringBookingId
        };
    }
}
