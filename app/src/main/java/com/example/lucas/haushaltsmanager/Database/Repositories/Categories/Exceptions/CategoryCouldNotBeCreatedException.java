package com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.Category;

public class CategoryCouldNotBeCreatedException extends EntityCouldNotBeCreatedException {
    public CategoryCouldNotBeCreatedException(Category category, Throwable previous) {
        super(String.format(
                "Category with id '%s' could not be created, reason: %s",
                category.getId().toString(),
                previous.getMessage()
        ), previous);
    }
}
