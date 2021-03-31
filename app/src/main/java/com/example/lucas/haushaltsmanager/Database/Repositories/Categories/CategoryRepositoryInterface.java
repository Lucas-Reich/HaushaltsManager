package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface CategoryRepositoryInterface {
    List<Category> getAll();

    void insert(Category category) throws CategoryCouldNotBeCreatedException;

    void update(Category updatedCategory) throws CategoryNotFoundException;

    void delete(Category category) throws SQLException;

    Category get(UUID id) throws CategoryNotFoundException;
}
