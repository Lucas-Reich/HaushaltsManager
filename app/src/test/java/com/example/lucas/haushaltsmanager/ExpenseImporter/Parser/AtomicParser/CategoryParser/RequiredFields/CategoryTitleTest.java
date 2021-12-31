package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

public class CategoryTitleTest {
    @Test
    public void returnsExpectedTranslationKey() {
        // Act
        IRequiredField field = new CategoryTitle();

        // Assert
        assertEquals(R.string.mapping_category_title, field.getTranslationKey());
    }
}
