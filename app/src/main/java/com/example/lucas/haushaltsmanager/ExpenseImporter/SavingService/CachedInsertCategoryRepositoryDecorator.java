package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CachedInsertCategoryRepositoryDecorator implements CategoryDAO {
    private final CategoryDAO repository;
    private final List<Category> cachedCategories = new ArrayList<>();

    CachedInsertCategoryRepositoryDecorator(CategoryDAO repository) {
        this.repository = repository;
    }

    @Override
    public Category get(@NonNull UUID id) {
        return repository.get(id);
    }

    @Override
    @NonNull
    public Category getByName(@NonNull String categoryName) {
        Category category = getCategoryByNameFromCache(categoryName);

        if (null != category) {
            return category;
        }

        return repository.getByName(categoryName);
    }

    @Override
    @NonNull
    public List<Category> getAll() {
        return repository.getAll();
    }

    @Override
    public void delete(@NonNull Category category) {
        repository.delete(category);
    }

    @Override
    public void update(@NonNull Category category) {
        repository.update(category);
    }

    @Override
    public void insert(@NonNull Category category) {
        Category cachedCategory = getCachedCategory(category);
        if (null != cachedCategory) {
            return;
        }

        repository.insert(category);
        cachedCategories.add(category);
    }

    @Nullable
    private Category getCategoryByNameFromCache(String categoryName) {
        for (Category existingCategory : cachedCategories) {
            if (!categoryName.equals(existingCategory.getName())) {
                continue;
            }

            return existingCategory;
        }

        return null;
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
