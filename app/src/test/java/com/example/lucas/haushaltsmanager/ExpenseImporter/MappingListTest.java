package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MappingListTest {
    private MappingList mappingList;

    @Before
    public void setUp() {
        mappingList = new MappingList();
    }

    @Test
    public void throwExceptionIfNoMappingIsFound() {
        try {
            mappingList.getMappingForKey("notExistingKey");

            Assert.fail("Found mapping for not existing Key.");
        } catch (NoMappingFoundException e) {

            assertEquals("No mapping defined for key 'notExistingKey'.", e.getMessage());
        }
    }

    @Test
    public void getMappingForExistingField() {
        String keyName = "existingKey";
        String expectedMapping = "keyMapping";

        mappingList.addMapping(mockMapping(keyName, expectedMapping));
        String actualMapping = mappingList.getMappingForKey(keyName);

        assertEquals(expectedMapping, actualMapping);
    }

    private KeyMappingInterface mockMapping(String key, String value) {
        KeyMappingInterface mapping = mock(KeyMappingInterface.class);
        when(mapping.getKey()).thenReturn(key);
        when(mapping.getMappedField()).thenReturn(value);

        return mapping;
    }
}
