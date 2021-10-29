package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

public class ExpenseNotFoundException extends EntityNotExistingException {
    private ExpenseNotFoundException(String message) {
        super(message);
    }

    public static ExpenseNotFoundException parentExpenseNotFoundException(IBooking childExpense) {
        return new ExpenseNotFoundException("Could not find ParentExpense for ChildExpense " + childExpense.getTitle() + ".");
    }
}
