package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.sql.SQLException;
import java.util.List;

public class MockCategoryRepository implements CategoryRepositoryInterface {
    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public Category insert(Category category) {
        return null;
    }

    @Override
    public void update(Category updatedCategory) throws CategoryNotFoundException {

    }

    @Override
    public void delete(Category category) throws SQLException {

    }

    @Override
    public Category get(long categoryId) throws CategoryNotFoundException {
        return null;
    }
}
