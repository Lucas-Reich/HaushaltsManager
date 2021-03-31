package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.UUID;

public class ChildExpenseNotFoundException extends EntityNotExistingException {
    public ChildExpenseNotFoundException(UUID id) {
        super(String.format("Could not find Child Booking with id %s.", id.toString()));
    }
}
