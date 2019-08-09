package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

import java.util.ArrayList;

public class CategoryParser implements IObjectParser<Category> {
    public static final String CATEGORY_TITEL_KEY = "category_title";

    public Category parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String categoryTitle = line.getAsString(mapping.getMappingForKey(CATEGORY_TITEL_KEY));

        assertNotEmpty(categoryTitle);

        return new Category(
                categoryTitle,
                Color.random(),
                true,
                new ArrayList<Category>()
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Category.class);
    }
}
