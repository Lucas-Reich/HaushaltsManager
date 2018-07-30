package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class ChildExpenseNotFoundException extends EntityNotExistingException {
    public ChildExpenseNotFoundException(long childId) {
        super("Child Expense with id " + childId + " does not exist.");
    }
}
