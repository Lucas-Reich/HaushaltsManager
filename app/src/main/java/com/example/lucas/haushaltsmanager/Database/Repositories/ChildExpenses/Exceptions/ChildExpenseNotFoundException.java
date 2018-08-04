package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class ChildExpenseNotFoundException extends EntityNotExistingException {
    public ChildExpenseNotFoundException(long childId) {
        super(String.format("Could not find Child Expense with id %s.", childId));
    }
}
