package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lucas.haushaltsmanager.entities.Category;

import java.util.List;
import java.util.UUID;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category get(UUID id);
}
