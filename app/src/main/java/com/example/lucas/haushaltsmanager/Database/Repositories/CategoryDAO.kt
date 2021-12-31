package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.Category
import java.util.*

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM categories")
    fun getAll(): List<Category>

    @Insert
    fun insert(category: Category)

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    fun get(id: UUID): Category?

    @Query("SELECT * FROM categories WHERE name = :categoryName")
    fun getByName(categoryName: String): Category
}