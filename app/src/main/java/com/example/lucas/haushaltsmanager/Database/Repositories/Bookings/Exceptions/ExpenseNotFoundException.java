package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;

import java.util.UUID;

public class ExpenseNotFoundException extends EntityNotExistingException {
    private ExpenseNotFoundException(String message) {
        super(message);
    }

    public static ExpenseNotFoundException expenseNotFoundException(UUID id) {
        return new ExpenseNotFoundException("Could not find Booking with id " + id.toString() + ".");
    }

    public static ExpenseNotFoundException couldNotUpdateReferencedExpense(UUID id) {
        return new ExpenseNotFoundException(String.format(
                "Failed to update expense with id '%s', as it was not found!",
                id.toString()
        ));
    }

    public static ExpenseNotFoundException parentExpenseNotFoundException(IBooking childExpense) {
        return new ExpenseNotFoundException("Could not find ParentExpense for ChildExpense " + childExpense.getTitle() + ".");
    }
}
