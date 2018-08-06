package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class CannotDeleteChildExpenseException extends Exception {
    private CannotDeleteChildExpenseException(String message) {
        super(message);
    }

    public static CannotDeleteChildExpenseException RelatedExpenseNotFound(ExpenseObject childExpense) {
        return new CannotDeleteChildExpenseException(String.format("Could not find Parent for Child Expense %s.", childExpense.getTitle()));
    }
}
