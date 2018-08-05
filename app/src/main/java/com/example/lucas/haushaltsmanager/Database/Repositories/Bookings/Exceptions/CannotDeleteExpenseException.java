package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class CannotDeleteExpenseException extends CouldNotDeleteEntityException {
    private CannotDeleteExpenseException(String message) {
        super(message);
    }

    public static CannotDeleteExpenseException BookingAttachedToChildException(ExpenseObject expense) {
        return new CannotDeleteExpenseException(String.format("Expense %s is attached to a child expense and cannot be deleted.", expense.getTitle()));
    }

    public static CannotDeleteExpenseException RelatedAccountDoesNotExist(ExpenseObject expense) {
        return new CannotDeleteExpenseException(String.format("Related Account with index %s attached to expense %s does not exist.", expense.getAccountId(), expense.getTitle()));
    }

    public static CannotDeleteExpenseException CannotDeleteChild(ExpenseObject childExpense) {
        return new CannotDeleteExpenseException(String.format("Child %s attached to account could not be deleted.", childExpense.getTitle()));
    }
}
