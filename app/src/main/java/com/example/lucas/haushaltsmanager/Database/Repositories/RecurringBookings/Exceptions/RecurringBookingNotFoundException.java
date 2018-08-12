package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class RecurringBookingNotFoundException extends EntityNotExistingException {
    public RecurringBookingNotFoundException(long recurringBookingId) {
        super("Cannot find Recurring Booking with id " + recurringBookingId + ".");
    }
}
