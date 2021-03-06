package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

class MockChildCategoryRepository implements ChildCategoryRepositoryInterface {
    @Override
    public boolean exists(Category childCategory) {
        return false;
    }

    @Override
    public Category get(long childCategoryId) throws ChildCategoryNotFoundException {
        return null;
    }

    @Override
    public List<Category> getAll(long parentCategoryId) {
        return new ArrayList<>();
    }

    @Override
    public Category insert(Category parentCategory, Category childCategory) {
        return null;
    }

    @Override
    public void delete(Category category) throws CannotDeleteChildCategoryException {

    }

    @Override
    public void update(Category category) throws ChildCategoryNotFoundException {

    }

    @Override
    public void hide(Category category) throws ChildCategoryNotFoundException {

    }
}
