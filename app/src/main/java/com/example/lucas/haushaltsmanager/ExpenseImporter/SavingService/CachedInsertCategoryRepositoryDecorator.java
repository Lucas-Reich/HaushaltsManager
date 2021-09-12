package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CachedInsertCategoryRepositoryDecorator implements CategoryDAO {
    private final CategoryDAO repository;
    private final List<Category> cachedCategories = new ArrayList<>();

    CachedInsertCategoryRepositoryDecorator(CategoryDAO repository) {
        this.repository = repository;
    }

    @Override
    public Category get(UUID id) {
        return repository.get(id);
    }

    @Override
    public List<Category> getAll() {
        return repository.getAll();
    }

    @Override
    public void delete(Category category) {
        repository.delete(category);
    }

    @Override
    public void update(Category category) {
        repository.update(category);
    }

    @Override
    public void insert(Category category) {
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
        return one.getName().equals(other.getName())
                && one.getDefaultExpenseType().equals(other.getDefaultExpenseType());
    }
}
