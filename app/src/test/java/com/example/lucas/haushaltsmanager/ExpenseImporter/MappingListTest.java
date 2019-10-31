package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class MappingListTest {
    private MappingList mappingList;

    @Before
    public void setUp() {
        mappingList = new MappingList();
    }

    @Test
    public void throwExceptionIfNoMappingIsFound() {
        try {
            mappingList.getMappingForKey(mock(IRequiredField.class));

            Assert.fail("Found mapping for not existing Key.");
        } catch (NoMappingFoundException e) {

            // Ich kann die Fehlernachricht nicht auswerten, da die RequiredField Klasse von Mockito auto generiert ist.
        }
    }

    @Test
    public void getMappingForExistingField() {
        // Set Up
        IRequiredField requiredField = mock(IRequiredField.class);
        int expectedMapping = 101;


        // Act
        mappingList.addMapping(requiredField, expectedMapping);
        int actualMapping = mappingList.getMappingForKey(requiredField);


        // Assert
        assertEquals(expectedMapping, actualMapping);
    }
}
