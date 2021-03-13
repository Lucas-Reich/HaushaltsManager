package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.List;

public interface ChildCategoryRepositoryInterface {
    Category get(long childCategoryId) throws ChildCategoryNotFoundException;

    List<Category> getAll(long parentCategoryId);

    Category insert(Category parentCategory, Category childCategory);

    void delete(Category category) throws CannotDeleteChildCategoryException;

    void update(Category category) throws ChildCategoryNotFoundException;

    void hide(Category category) throws ChildCategoryNotFoundException;
}
