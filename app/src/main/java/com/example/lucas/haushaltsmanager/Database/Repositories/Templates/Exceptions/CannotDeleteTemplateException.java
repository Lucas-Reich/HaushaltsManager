package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;

public class CannotDeleteTemplateException extends CouldNotDeleteEntityException {
    public CannotDeleteTemplateException(Template template) {
        super("Template " + template.getTemplate().getTitle() + " cannot be deleted.");
    }
}
