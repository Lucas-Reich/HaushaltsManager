package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public class ExpenseNotFoundException extends EntityNotExistingException {
    private ExpenseNotFoundException(String message) {
        super(message);
    }

    public static ExpenseNotFoundException expenseNotFoundException(long expenseId) {
        return new ExpenseNotFoundException("Could not find Booking with id " + expenseId + ".");
    }

    public static ExpenseNotFoundException parentExpenseNotFoundException(ExpenseObject childExpense) {
        return new ExpenseNotFoundException("Could not find ParentExpense for ChildExpense " + childExpense.getTitle() + ".");
    }
}
