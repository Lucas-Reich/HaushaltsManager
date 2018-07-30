package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class CannotDeleteExpenseException extends CouldNotDeleteEntityException {
    public CannotDeleteExpenseException(ExpenseObject expense) {
        super("Expense " + expense.getTitle() + " cannot be deleted.");
    }
}
