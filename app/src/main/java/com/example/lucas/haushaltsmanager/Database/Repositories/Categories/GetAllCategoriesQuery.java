package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllCategoriesQuery implements QueryInterface {

    @Override
    public String sql() {
        return "SELECT id, name, color, default_expense_type FROM CATEGORIES WHERE hidden = 0";
    }

    @Override
    public Object[] values() {
        return new Object[]{};
    }
}
