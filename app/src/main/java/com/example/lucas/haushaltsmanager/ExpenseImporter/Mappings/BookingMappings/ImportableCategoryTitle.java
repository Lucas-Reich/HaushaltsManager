package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportableCategoryTitle implements KeyMappingInterface {
    private final String mapping;

    public ImportableCategoryTitle(String mapping) {

        this.mapping = mapping;
    }

    @Override
    public String getKey() {
        return CategoryParser.CATEGORY_TITEL_KEY;
    }

    @Override
    public String getMappedField() {
        return mapping;
    }
}