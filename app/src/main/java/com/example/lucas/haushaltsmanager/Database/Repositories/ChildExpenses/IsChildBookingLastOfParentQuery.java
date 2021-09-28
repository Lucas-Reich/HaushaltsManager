package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

class IsChildBookingLastOfParentQuery implements QueryInterface {
    private final Booking childBooking;

    public IsChildBookingLastOfParentQuery(Booking childBooking) {
        this.childBooking = childBooking;
    }

    @Override
    public String sql() {
        String subQuery = "(SELECT parent_id FROM BOOKINGS WHERE id = '%s')";

        return "SELECT * FROM BOOKINGS WHERE parent_id = " + subQuery;
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childBooking.getId().toString()
        };
    }
}
