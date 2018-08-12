package com.example.lucas.haushaltsmanager.Entities;

public class InvalidExpenseTypeException extends Exception {
    public InvalidExpenseTypeException(String expenseType) {
        super("ExpenseType " + expenseType + " does not exist.");
    }
}
