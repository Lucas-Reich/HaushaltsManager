package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class ExpenseNotFoundException extends EntityNotExistingException {
    public ExpenseNotFoundException(long expenseId) {
        super("Could not find Expense with id " + expenseId + ".");
    }
}
