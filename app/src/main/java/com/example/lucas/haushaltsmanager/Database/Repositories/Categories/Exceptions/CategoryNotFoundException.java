package com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.UUID;

public class CategoryNotFoundException extends EntityNotExistingException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Could not find Category with index " + categoryId.toString() + ".");
    }
}
