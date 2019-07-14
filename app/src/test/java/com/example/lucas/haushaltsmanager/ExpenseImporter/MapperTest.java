package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidMappingException;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;

public class MapperTest {
    private Mapper mapper;

    @Before
    public void setUp() {
        mapper = new Mapper();
    }

    @Test
    public void mapperCreatesCorrectMap() {
        String line = "eins,zwei,drei";
        FieldMapping mapping = new FieldMapping();

        Field field1 = mock(Field.class);
        mapping.createMapping(field1, 1);

        Field field2 = mock(Field.class);
        mapping.createMapping(field2, 0);

        Field field3 = mock(Field.class);
        mapping.createMapping(field3, 2);

        HashMap<Field, String> map = mapper.createMapping(line, mapping);

        assertEquals("zwei", map.get(field1));
        assertEquals("eins", map.get(field2));
        assertEquals("drei", map.get(field3));
    }

    @Test
    public void mapperHandlesLineWithEmptyFields() {
        String lineWithEmptyFields = ",,,sieben";
        FieldMapping mapping = new FieldMapping();
        Field field1 = mock(Field.class);
        mapping.createMapping(field1, 0);

        Field field2 = mock(Field.class);
        mapping.createMapping(field2, 1);

        Field field3 = mock(Field.class);
        mapping.createMapping(field3, 2);

        Field field4 = mock(Field.class);
        mapping.createMapping(field4, 3);

        HashMap<Field, String> map = mapper.createMapping(lineWithEmptyFields, mapping);

        assertEquals("", map.get(field1));
        assertEquals("", map.get(field2));
        assertEquals("", map.get(field3));
        assertEquals("sieben", map.get(field4));
    }

    @Test
    public void mapperCreatesEmptyMapForEmptyMapping() {
        String line = "eins,zwei,drei,vier,fünf";
        FieldMapping mapping = new FieldMapping();

        HashMap<Field, String> map = mapper.createMapping(line, mapping);

        assertTrue(map.isEmpty());
    }

    @Test
    public void mapperThrowsExceptionIfMappingHasMoreFieldsThanLine() {
        String line = "eins,zwei";

        FieldMapping mapping = new FieldMapping();
        mapping.createMapping(mock(Field.class), 0);
        mapping.createMapping(mock(Field.class), 1);
        mapping.createMapping(mock(Field.class), 2);

        try {
            mapper.createMapping(line, mapping);
            fail("Could create mapping from line with missing value");
        } catch (InvalidMappingException e) {
            assertEquals("Line does not have enough fields to map. Given: 2, expected: 3.", e.getMessage());
        }
    }

    @Test
    public void mapperThrowsExceptionForNotExistingIndex() {
        String line = "eins";

        FieldMapping mapping = new FieldMapping();
        mapping.createMapping(mock(Field.class), 500);

        try {
            mapper.createMapping(line, mapping);
            fail("Could create mapping from line with missing value");
        } catch (InvalidMappingException e) {
            assertEquals("Expected value at index: 500, but found none.", e.getMessage());
        }
    }
}
