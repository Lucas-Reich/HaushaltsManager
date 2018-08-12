package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotCreateEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class CouldNotCreateTemplateException extends CouldNotCreateEntityException {
    public CouldNotCreateTemplateException(String message) {
        super(message);
    }

    public static CouldNotCreateTemplateException relatedExpenseNotFound(ExpenseObject relatedExpense) {
        return new CouldNotCreateTemplateException(String.format("Could not find related Expense %s", relatedExpense.getTitle()));
    }
}
