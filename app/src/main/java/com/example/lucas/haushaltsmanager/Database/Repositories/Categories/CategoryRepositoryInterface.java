package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.List;

public interface CategoryRepositoryInterface {
    List<Category> getAll();

    Category insert(Category category);

    void update(Category updatedCategory)throws CategoryNotFoundException;
}
