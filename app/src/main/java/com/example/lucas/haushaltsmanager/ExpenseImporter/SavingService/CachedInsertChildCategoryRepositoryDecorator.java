package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class CachedInsertChildCategoryRepositoryDecorator implements ChildCategoryRepositoryInterface {
    private ChildCategoryRepositoryInterface repository;
    private HashMap<Category, List<Category>> cachedCategories = new HashMap<>();

    public CachedInsertChildCategoryRepositoryDecorator(ChildCategoryRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(Category childCategory) {
        return repository.exists(childCategory);
    }

    @Override
    public Category get(long childCategoryId) throws ChildCategoryNotFoundException {
        return repository.get(childCategoryId);
    }

    @Override
    public List<Category> getAll(long parentCategoryId) {
        return repository.getAll(parentCategoryId);
    }

    @Override
    public Category insert(Category parentCategory, Category childCategory) {
        Category createdCategory = getCategoryFromCache(parentCategory, childCategory);

        if (null == createdCategory) {
            createdCategory = repository.insert(parentCategory, childCategory);
            addToCache(parentCategory, createdCategory);
        }

        return createdCategory;
    }

    @Override
    public void delete(Category category) throws CannotDeleteChildCategoryException {
        repository.delete(category);
    }

    @Override
    public void update(Category category) throws ChildCategoryNotFoundException {
        repository.update(category);
    }

    @Override
    public void hide(Category category) throws ChildCategoryNotFoundException {
        repository.hide(category);
    }

    private void addToCache(Category parentCategory, Category childCategory) {
        List<Category> cachedChildCategories;

        if (cachedCategories.containsKey(parentCategory)) {
            cachedChildCategories = cachedCategories.get(parentCategory);
        } else {
            cachedChildCategories = new ArrayList<>();
        }

        cachedChildCategories.add(childCategory);
        cachedCategories.put(parentCategory, cachedChildCategories);
    }

    @Nullable
    private Category getCategoryFromCache(Category parentCategory, Category childCategory) {
        if (!cachedCategories.containsKey(parentCategory)) {
            return null;
        }

        for (Category existingCategory : cachedCategories.get(parentCategory)) {
            if (areEquals(existingCategory, childCategory)) {
                return existingCategory;
            }
        }

        return null;
    }

    private boolean areEquals(Category one, Category other) {
        return one.getTitle().equals(other.getTitle())
                && one.getDefaultExpenseType() == other.getDefaultExpenseType()
                && one.getChildren().equals(other.getChildren());
    }
}
