package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.UUID;

public class RecurringBookingNotFoundException extends EntityNotExistingException {
    public RecurringBookingNotFoundException(UUID id) {
        super("Cannot find Recurring Booking with id " + id.toString() + ".");
    }
}
