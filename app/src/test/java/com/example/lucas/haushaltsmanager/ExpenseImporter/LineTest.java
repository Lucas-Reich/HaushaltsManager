package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LineTest {

    @Test
    public void getLineReturnsUnchangedLine() {
        String expectedLineString = "eins,zwei,drei,vier,fünf";
        Line line = new Line(expectedLineString, 1, new FieldMapping());

        assertEquals(expectedLineString, line.getLine());
    }

    @Test
    public void getLineNumberReturnsUnchangedLineNumber() {
        int expectedLineNumber = 100;
        Line line = new Line("", expectedLineNumber, new FieldMapping());

        assertEquals(expectedLineNumber, line.getLineNumber());
    }

    @Test
    public void fillAssignsCorrectValue() {
        FieldMapping mapping = new FieldMapping();
        Field field = mock(Field.class);
        mapping.createMapping(field, 0);

        Line line = new Line("eins", 1, mapping);

        line.fill(field);

        verify(field, times(1)).set("eins");
    }

    @Test
    public void fillThrowsExceptionIfNoMappingForFieldExists() {
        Line line = new Line("eins,zwei", 1, new FieldMapping());

        try {
            line.fill(new DummyField());

            fail("Could set value of not existing Field.");
        } catch (IllegalArgumentException e) {
            assertEquals("Line does not contain mapping for field DummyField", e.getMessage());
        }
    }
}
