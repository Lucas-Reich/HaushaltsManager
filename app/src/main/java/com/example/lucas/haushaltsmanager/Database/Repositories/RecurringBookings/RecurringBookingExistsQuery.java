package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

class RecurringBookingExistsQuery implements QueryInterface {
    private final RecurringBooking recurringBooking;

    public RecurringBookingExistsQuery(RecurringBooking recurringBooking) {
        this.recurringBooking = recurringBooking;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                recurringBooking.getIndex(),
                recurringBooking.getBooking().getIndex(),
                recurringBooking.getExecutionDate().getTimeInMillis(),
                recurringBooking.getEnd().getTimeInMillis(),
                recurringBooking.getFrequency().getCalendarField(),
                recurringBooking.getFrequency().getAmount()
        };
    }
}
