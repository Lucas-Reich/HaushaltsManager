package com.example.lucas.haushaltsmanager.Database.Repositories

import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.entities.category.Category
import com.example.lucas.haushaltsmanager.entities.category.TransferCategory
import com.example.lucas.haushaltsmanager.entities.category.UnassignedCategory
import java.util.*

class CategoryRepository(val categoryDAO: CategoryDAO) {
    fun getAll(): List<Category> {
        return categoryDAO.getAll()
    }

    fun insert(category: Category) {
        categoryDAO.insert(category)
    }

    fun update(category: Category) {
        categoryDAO.update(category)
    }

    fun delete(category: Category) {
        categoryDAO.delete(category)
    }

    fun get(id: UUID): Category? {
        if (app.notAssignedCategoryId.equals(id)) {
            return UnassignedCategory()
        }

        if (app.transferCategoryId.equals(id)) {
            return TransferCategory()
        }

        return categoryDAO.get(id)
    }
}