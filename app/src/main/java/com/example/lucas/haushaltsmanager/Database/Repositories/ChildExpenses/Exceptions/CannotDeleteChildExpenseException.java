package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public class CannotDeleteChildExpenseException extends Exception {
    private CannotDeleteChildExpenseException(String message) {
        super(message);
    }

    public static CannotDeleteChildExpenseException RelatedExpenseNotFound(Booking childExpense) {
        return new CannotDeleteChildExpenseException(String.format("Could not find Parent for Child Booking %s.", childExpense.getTitle()));
    }
}
