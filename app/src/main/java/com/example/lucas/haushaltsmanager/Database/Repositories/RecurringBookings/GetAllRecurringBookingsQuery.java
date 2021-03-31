package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import java.util.Calendar;

class GetAllRecurringBookingsQuery implements QueryInterface {
    private final Calendar start;
    private final Calendar end;

    public GetAllRecurringBookingsQuery(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String sql() {
        return "SELECT id, calendar_field, amount, start, end "
                + "FROM RECURRING_BOOKINGS "
                + "WHERE start BETWEEN %s AND %s;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                start.getTimeInMillis(),
                end.getTimeInMillis()
        };
    }
}
