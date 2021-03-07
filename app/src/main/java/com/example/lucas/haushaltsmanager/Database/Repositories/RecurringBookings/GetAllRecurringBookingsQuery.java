package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
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
        return "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE
                + " BETWEEN %s AND %s;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                start.getTimeInMillis(),
                end.getTimeInMillis()
        };
    }
}
