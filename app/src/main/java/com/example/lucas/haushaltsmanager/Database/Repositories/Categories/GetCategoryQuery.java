package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import java.util.UUID;

class GetCategoryQuery implements QueryInterface {
    private final UUID categoryId;

    public GetCategoryQuery(UUID id) {
        this.categoryId = id;
    }

    @Override
    public String sql() {
        return "SELECT id, name, color, default_expense_type FROM CATEGORIES WHERE id = '%s'";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                categoryId.toString()
        };
    }
}
