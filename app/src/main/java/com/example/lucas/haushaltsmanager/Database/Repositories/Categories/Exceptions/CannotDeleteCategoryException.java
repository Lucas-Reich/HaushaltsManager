package com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Category;

public class CannotDeleteCategoryException extends CouldNotDeleteEntityException {
    public CannotDeleteCategoryException(Category category) {
        super("Category " + category.getTitle() + " cannot be deleted.");
    }
}
