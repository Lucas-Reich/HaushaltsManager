package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CachedInsertCategoryRepositoryDecorator implements CategoryRepositoryInterface {
    private final CategoryRepositoryInterface repository;
    private final List<Category> cachedCategories = new ArrayList<>();

    CachedInsertCategoryRepositoryDecorator(CategoryRepositoryInterface repository) {
        this.repository = repository;
    }

    @Override
    public Category get(UUID id) throws CategoryNotFoundException {
        return repository.get(id);
    }

    @Override
    public List<Category> getAll() {
        return repository.getAll();
    }

    @Override
    public void delete(Category category) throws SQLException {
        repository.delete(category);
    }

    @Override
    public void update(Category category) throws CategoryNotFoundException {
        repository.update(category);
    }

    @Override
    public void insert(Category category) throws CategoryCouldNotBeCreatedException {
        Category cachedCategory = getCachedCategory(category);
        if (null != cachedCategory) {
            return;
        }

        repository.insert(category);
        cachedCategories.add(category);
    }

    @Nullable
    private Category getCachedCategory(Category category) {
        for (Category cachedCategory : cachedCategories) {
            if (areEquals(category, cachedCategory)) {
                return cachedCategory;
            }
        }

        return null;
    }

    private boolean areEquals(Category one, Category other) {
        return one.getTitle().equals(other.getTitle())
                && one.getDefaultExpenseType().equals(other.getDefaultExpenseType());
    }
}
