package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

public class AddChildToChildException extends Exception {
    public AddChildToChildException() {
        super("It is not possible to add children to a ChildExpense.");
    }
}
