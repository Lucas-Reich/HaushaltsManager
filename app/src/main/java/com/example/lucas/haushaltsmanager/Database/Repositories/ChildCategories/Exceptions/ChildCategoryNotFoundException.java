package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

public class ChildCategoryNotFoundException extends EntityNotExistingException {
    public ChildCategoryNotFoundException(long childCategoryId) {
        super("Could not find Child Category with index " + childCategoryId + ".");
    }
}
