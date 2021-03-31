package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import java.util.UUID;

class GetRecurringBookingQuery implements QueryInterface {
    private final UUID recurringBookingId;

    public GetRecurringBookingQuery(UUID id) {
        this.recurringBookingId = id;
    }

    @Override
    public String sql() {
        return "SELECT id, calendar_field, amount, start, end "
                + " FROM RECURRING_BOOKINGS "
                + " WHERE id = '%s'";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                recurringBookingId.toString()
        };
    }
}
