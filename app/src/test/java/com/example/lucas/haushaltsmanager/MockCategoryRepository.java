package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MockCategoryRepository implements CategoryRepositoryInterface {
    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public void insert(Category category) {

    }

    @Override
    public void update(Category updatedCategory) throws CategoryNotFoundException {

    }

    @Override
    public void delete(Category category) throws SQLException {

    }

    @Override
    public Category get(UUID id) throws CategoryNotFoundException {
        return null;
    }
}
