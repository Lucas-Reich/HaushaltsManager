package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.CategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import java.util.Collections;
import java.util.List;

public class CategoryParser implements IParser<Category> {
    public static final IRequiredField CATEGORY_TITLE_KEY = new CategoryTitle();

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(CATEGORY_TITLE_KEY);
    }

    @Override
    @NonNull
    public Category parse(@NonNull Line line, @NonNull MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String categoryTitle = line.getAsString(mapping.getMappingForKey(CATEGORY_TITLE_KEY));

        assertNotEmpty(categoryTitle);

        return new Category(
                categoryTitle,
                Color.Companion.random(),
                ExpenseType.Companion.expense()
        );
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Category.class);
    }
}
