package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SemicolonTest {
    @Test
    public void shouldReturnSemicolonAsDelimiter() {
        IDelimiter delimiter = new Semicolon();

        assertEquals(";", delimiter.getDelimiter());
    }
}
