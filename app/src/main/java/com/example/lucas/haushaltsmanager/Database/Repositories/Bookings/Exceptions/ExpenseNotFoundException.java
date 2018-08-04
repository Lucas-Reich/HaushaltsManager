package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class ExpenseNotFoundException extends EntityNotExistingException {
    private ExpenseNotFoundException(String message) {
        super(message);
    }

    public static ExpenseNotFoundException expenseNotFoundException(long expenseId) {
        return new ExpenseNotFoundException("Could not find Expense with id " + expenseId + ".");
    }

    public static ExpenseNotFoundException parentExpenseNotFoundException(ExpenseObject childExpense) {
        return new ExpenseNotFoundException("Could not find ParentExpense for ChildExpense " + childExpense.getTitle() + ".");
    }
}
