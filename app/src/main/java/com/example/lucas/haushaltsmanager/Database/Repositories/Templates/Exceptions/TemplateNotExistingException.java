package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class TemplateNotExistingException extends EntityNotExistingException {
    public TemplateNotExistingException(long templateId) {
        super("Cannot find template with id " + templateId);
    }
}
