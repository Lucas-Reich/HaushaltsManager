package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Entities.Category;

public class CannotDeleteChildCategoryException extends CouldNotDeleteEntityException {

    private CannotDeleteChildCategoryException(String message) {
        super(message);
    }

    public static CannotDeleteChildCategoryException childCategoryAttachedToParentExpenseException(Category category) {
        return new CannotDeleteChildCategoryException("Child category " + category.getTitle() + " is attached to a ParentExpense and cannot be deleted.");
    }

    public static CannotDeleteChildCategoryException childCategoryAttachedToChildExpenseException(Category category) {
        return new CannotDeleteChildCategoryException("Child category " + category.getTitle() + " is attached to a ChildExpense and cannot be deleted.");
    }

    public static CannotDeleteChildCategoryException childCategoryParentNotFoundException(Category category) {
        return new CannotDeleteChildCategoryException("Parent of the Child category " + category.getTitle() + " was not found.");
    }
}
