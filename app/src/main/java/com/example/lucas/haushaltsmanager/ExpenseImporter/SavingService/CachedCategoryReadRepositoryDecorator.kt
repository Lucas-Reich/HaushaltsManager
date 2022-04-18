package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService

import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO
import com.example.lucas.haushaltsmanager.entities.category.Category
import java.util.*

class CachedCategoryReadRepositoryDecorator(
    private val repository: CategoryDAO
) : CategoryDAO {
    private val cache: MutableList<Category> = ArrayList()

    override fun getAll(): List<Category> {
        return repository.getAll()
    }

    override fun insert(category: Category) {
        val cachedCategory = cache.singleOrNull { it.name == category.name && it.defaultExpenseType == category.defaultExpenseType }
        if (null != cachedCategory) {
            return
        }

        repository.insert(category)
        cache.add(category)
    }

    override fun update(category: Category) {
        repository.update(category)
    }

    override fun delete(category: Category) {
        repository.delete(category)
    }

    override fun get(id: UUID): Category? {
        val cachedCategory = cache.singleOrNull() { it.id == id }
        if (null != cachedCategory) {
            return cachedCategory
        }

        return repository.get(id)
    }

    override fun getByName(categoryName: String): Category {
        val cachedCategory = cache.singleOrNull { it.name == categoryName }
        if (null != cachedCategory) {
            return cachedCategory
        }

        return repository.getByName(categoryName)
    }
}