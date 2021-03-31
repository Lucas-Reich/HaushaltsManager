package com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.TemplateBooking;

public class TemplateCouldNotBeCreatedException extends EntityCouldNotBeCreatedException {
    public TemplateCouldNotBeCreatedException(TemplateBooking template, Throwable previous) {
        super(String.format(
                "Could not create template with id: '%s', reason: %s",
                template.getId().toString(),
                previous.getMessage()
        ), previous);
    }
}
