package com.example.lucas.haushaltsmanager.ExpenseImporter;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Before;
import org.junit.Test;

public class MappingListTest {
    private MappingList mappingList;

    @Before
    public void setUp() {
        mappingList = new MappingList();
    }

    @Test
    public void throwExceptionIfNoMappingIsFound() {
        try {
            mappingList.getMappingForKey(new TestRequiredField());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'TestRequiredField'.", e.getMessage());
        }
    }

    @Test
    public void getMappingForExistingField() {
        // Arrange
        IRequiredField requiredField = new TestRequiredField();
        int expectedMapping = 101;

        // Act
        mappingList.addMapping(requiredField, expectedMapping);
        int actualMapping = mappingList.getMappingForKey(requiredField);

        // Assert
        assertEquals(expectedMapping, actualMapping);
    }

    private static class TestRequiredField implements IRequiredField {
        @Override
        public int getTranslationKey() {
            return 0;
        }
    }
}
