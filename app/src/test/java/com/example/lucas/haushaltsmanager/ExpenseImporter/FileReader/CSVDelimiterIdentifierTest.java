package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Semicolon;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.DelimiterIdentifier.CSVDelimiterIdentifier;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CSVDelimiterIdentifierTest {
    private CSVDelimiterIdentifier CSVDelimiterIdentifier;

    @Before
    public void setUp() {
        CSVDelimiterIdentifier = new CSVDelimiterIdentifier();
    }

    @Test
    public void correctlyHandlesNoDelimiter() {
        String stringWithDelimiter = generateStringWithDelimiter("");

        IDelimiter actualDelimiter = CSVDelimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, CSVDelimiterIdentifier.DEFAULT_DELIMITER);
    }

    @Test
    public void correctlyHandlesSameDelimiterCount() {
        IDelimiter expectedDelimiter = new Comma();
        String stringWithDelimiter = String.join(expectedDelimiter.getDelimiter(), "1;", "2;", "3;", "4;", "5;", "6;", "7;", "8;", "9");

        IDelimiter actualDelimiter = CSVDelimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    @Test
    public void correctlyHandlesNotSupportedDelimiter() {
        String notSupportedDelimiter = "|";
        String stringWithDelimiter = generateStringWithDelimiter(notSupportedDelimiter);

        IDelimiter actualDelimiter = CSVDelimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, CSVDelimiterIdentifier.DEFAULT_DELIMITER);
    }

    @Test
    public void correctlyIdentifiesCommaAsDelimiter() {
        IDelimiter expectedDelimiter = new Comma();
        String stringWithDelimiter = generateStringWithDelimiter(expectedDelimiter.getDelimiter());

        IDelimiter actualDelimiter = CSVDelimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    @Test
    public void correctlyIdentifiesSemicolonAsDelimiter() {
        IDelimiter expectedDelimiter = new Semicolon();
        String stringWithDelimiter = generateStringWithDelimiter(expectedDelimiter.getDelimiter());

        IDelimiter actualDelimiter = CSVDelimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    private String generateStringWithDelimiter(String delimiter) {
        return String.join(delimiter, "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }

    private void assertSameInstance(Object actual, Object expected) {
        assertEquals(actual.getClass(), expected.getClass());
    }
}
