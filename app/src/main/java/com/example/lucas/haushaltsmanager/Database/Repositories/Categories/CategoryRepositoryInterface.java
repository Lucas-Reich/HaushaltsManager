package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.List;

public interface CategoryRepositoryInterface {
    boolean exists(Category category);

    Category get(long categoryId)throws CategoryNotFoundException;

    List<Category> getAll();

    Category insert(Category category);

    void update(Category updatedCategory)throws CategoryNotFoundException;
}
