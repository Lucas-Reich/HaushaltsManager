package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

public class CannotDeleteTemplateException extends CouldNotDeleteEntityException {
    public CannotDeleteTemplateException(ExpenseObject template) {
        super("Template " + template.getTitle() + " cannot be deleted.");
    }
}
