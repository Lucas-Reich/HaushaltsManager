package com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class CategoryNotFoundException extends EntityNotExistingException {
    public CategoryNotFoundException(long categoryId) {
        super("Could not find Category with index " + categoryId + ".");
    }
}
