package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Category;

public class CannotDeleteChildCategoryException extends CouldNotDeleteEntityException {

    public CannotDeleteChildCategoryException(String message, Throwable previous) {
        super(message, previous);
    }

    public static CannotDeleteChildCategoryException childCategoryParentNotFoundException(Category category) {
        String message = String.format(
                "Parent of the Child category %s was not found.",
                category.getTitle()
        );

        return new CannotDeleteChildCategoryException(message, null);
    }
}
