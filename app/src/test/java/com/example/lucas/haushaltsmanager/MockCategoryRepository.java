package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;

import java.util.List;
import java.util.UUID;

public class MockCategoryRepository implements CategoryDAO {
    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public void insert(Category category) {

    }

    @Override
    public void update(Category updatedCategory) {

    }

    @Override
    public void delete(Category category) {

    }

    @Override
    public Category get(UUID id) {
        return null;
    }
}
