package com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVTransformerTest {
    @Test
    public void transformerTransformsString() {
        // SetUp
        ITransformer transformer = new CSVTransformer("1;2;3");

        // Act
        Line line = transformer.transform("1;2;3");

        // Assert
        assertEquals("1", line.getAsString("1"));
        assertEquals("2", line.getAsString("2"));
        assertEquals("3", line.getAsString("3"));
    }

    @Test
    public void transformerThrowsExceptionForInvalidInput() {
        ITransformer transformer = new CSVTransformer("1,2,3");

        try {
            transformer.transform("1,2");

            Assert.fail("Invalid input could be transformed.");
        } catch (InvalidLineException e) {

            assertEquals("Could not import malformed line. Expected 3 arguments, got 2 arguments.", e.getMessage());
        }
    }
}
