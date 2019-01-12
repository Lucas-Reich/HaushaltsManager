package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public class AddChildToChildException extends Exception {
    public AddChildToChildException(ExpenseObject child, ExpenseObject parent) {
        super(
                String.format("It's not possible to addItem %s to %s, since %s is already a ChildExpense", child.getTitle(), parent.getTitle(), parent.getTitle())
        );
    }
}
