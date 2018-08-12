package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class TemplateNotFoundException extends EntityNotExistingException {
    public TemplateNotFoundException(long templateId) {
        super(String.format("Cannot find Template with id %s.", templateId));
    }
}
