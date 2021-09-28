package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

public class CannotDeleteExpenseException extends CouldNotDeleteEntityException {
    private CannotDeleteExpenseException(String message) {
        super(message, null);
    }

    public static CannotDeleteExpenseException BookingAttachedToChildException(IBooking expense) {
        return new CannotDeleteExpenseException(String.format("Booking %s is attached to a child expense and cannot be deleted.", expense.getTitle()));
    }
}
