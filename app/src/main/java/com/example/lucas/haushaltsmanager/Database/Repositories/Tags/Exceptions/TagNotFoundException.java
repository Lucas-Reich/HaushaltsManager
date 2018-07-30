package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class TagNotFoundException extends EntityNotExistingException {
    public TagNotFoundException(long index) {
        super("Could not find Tag with id " + index + ".");
    }
}
