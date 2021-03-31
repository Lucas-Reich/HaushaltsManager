package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

public class RecurringBookingCouldNotBeCreatedException extends EntityCouldNotBeCreatedException {
    public RecurringBookingCouldNotBeCreatedException(RecurringBooking recurringBooking, Throwable previous) {
        super(String.format(
                "Could not create RecurringBooking with id '%s', reason: %s",
                recurringBooking.getId().toString(),
                previous.getMessage()
        ), previous);
    }
}
