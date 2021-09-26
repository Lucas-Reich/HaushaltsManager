package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.New;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Semicolon;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.CSVFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.CSVFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

import org.junit.Test;

import java.io.FileNotFoundException;

public class CSVFileReaderTest {
    private static final String TEST_FILE_DIR = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/CsvFiles";
    // TODO: Kann ich die Dateien auch on the fly erstellen?

    @Test
    public void returnsCorrectAmountOfLines() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("fileWithSemicolonDelimiter"));

        int actualLineCount = reader.getLineCount();

        assertEquals(7, actualLineCount);
    }

    @Test
    public void getHeaderLineReturnsFirstLineOfFile() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("importableExpenseObjects"));

        String actualHeaderLine = reader.getHeaderLine();

        assertEquals("title,amount,date,category,account", actualHeaderLine);
    }

    @Test
    public void getCurrentLineReturnCorrectLine() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("fileWithSemicolonDelimiter"));

        moveReaderXTimes(reader, 2);

        Line actualLine = reader.getCurrentLine();
        assertLine("Meine Geile Buchung;100.00;09-08-2019 08:00:00;Haus;Girokonto", actualLine, new Semicolon());
    }

    @Test
    public void getCurrentLineWithoutMovingReturnsNull() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("fileWithSemicolonDelimiter"));

        Line actualLine = reader.getCurrentLine();

        assertNull(actualLine);
    }

    @Test
    public void getCurrentLineReturnsLastLineIfReaderIsPastLastLine() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("importableExpensesWithoutHeaderLine"));

        moveReaderXTimes(reader, 100);

        Line actualLine = reader.getCurrentLine();
        assertLine("Meine Geile Buchung,100.00,09-08-2019 08:00:00,Essen,,Sparschwein", actualLine, new Comma());
    }

    @Test
    public void cannotMoveToNextLineIfReaderIsClosed() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("importableExpenseObjects"));

        reader.close();

        assertFalse(reader.moveToNext());
    }

    @Test
    public void cannotMovePastLastLine() throws Exception {
        IFileReader reader = new CSVFileReader(getFile("CsvFileWithInvalidEntries"));

        int lineCount = reader.getLineCount();
        moveReaderXTimes(reader, lineCount);

        assertFalse(reader.moveToNext());
    }

    private void assertLine(String expected, Line actual, IDelimiter delimiter) {
        String[] expectedValues = expected.split(delimiter.getDelimiter(), -1);

        for (int i = 0; i < expectedValues.length; i++) {
            assertEquals(expectedValues[i], actual.getAsString(i));
        }
    }

    private void moveReaderXTimes(IFileReader reader, int times) {
        for (int i = 0; i < times; i++) {
            reader.moveToNext();
        }
    }

    private CSVFile getFile(String fileName) throws FileNotFoundException {
        return CSVFile.open(String.format(
                "%s/%s.csv",
                TEST_FILE_DIR,
                fileName
        ));
    }
}
