package com.example.lucas.haushaltsmanager.ExpenseImporter.Files.New;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.File;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.FileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.IFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.IFileReader;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class FileReaderTest {
    private static final String TEST_FILE_DIR = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/Files/CsvFiles";
    // TODO: Kann ich die Dateien auch on the fly erstellen?

    @Test
    public void throwsExceptionForInvalidFileType() throws FileNotFoundException {
        IFile file = new File("fileWithInvalidExtension.invalid");

        try {
            FileReader.read(file);

            Assert.fail("Could open file with invalid extension");
        } catch (InvalidFileException e) {

            assertEquals("Could not open file: fileWithInvalidExtension.invalid. Expected file of type csv.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionForNotExistingFile() {
        try {
            FileReader.read(new File("not/existing/file.csv"));

            Assert.fail("Not existing File could be opened");
        } catch (FileNotFoundException e) {

            assertEquals("not/existing/file.csv (No such file or directory)", e.getMessage());
        }
    }

    @Test
    public void canOpenFile() {
        try {
            FileReader.read(getFile("importableExpenseObjects"));
        } catch (Exception e) {

            Assert.fail("Could not open valid file");
        }
    }

    @Test
    public void returnsCorrectAmountOfLines() throws Exception {
        IFileReader reader = FileReader.read(getFile("fileWithSemicolonDelimiter"));

        int actualLineCount = reader.getLineCount();

        assertEquals(7, actualLineCount);
    }

    @Test
    public void getHeaderLineReturnsFirstLineOfFile() throws Exception {
        IFileReader reader = FileReader.read(getFile("importableExpenseObjects"));

        String actualHeaderLine = reader.getHeaderLine();

        assertEquals("title,amount,date,category,notice,account", actualHeaderLine);
    }

    @Test
    public void getCurrentLineReturnCorrectLine() throws Exception {
        IFileReader reader = FileReader.read(getFile("fileWithSemicolonDelimiter"));

        moveReaderXTimes(reader, 2);

        String actualLine = reader.getCurrentLine();
        assertEquals("Meine Geile Buchung;100.00;09-08-2019 08:00:00;Haus;;Girokonto", actualLine);
    }

    @Test
    public void getCurrentLineWithoutMovingReturnsNull() throws Exception {
        IFileReader reader = FileReader.read(getFile("fileWithSemicolonDelimiter"));

        String actualLine = reader.getCurrentLine();

        assertNull(actualLine);
    }

    @Test
    public void getCurrentLineReturnsLastLineIfReaderIsPastLastLine() throws Exception {
        IFileReader reader = FileReader.read(getFile("importableExpensesWithoutHeaderLine"));

        moveReaderXTimes(reader, 100);

        String actualLine = reader.getCurrentLine();
        assertEquals("Meine Geile Buchung,100.00,09-08-2019 08:00:00,Essen,,Sparschwein", actualLine);
    }

    @Test
    public void cannotMoveToNextLineIfReaderIsClosed() throws Exception {
        IFileReader reader = FileReader.read(getFile("importableExpenseObjects"));

        reader.close();

        assertFalse(reader.moveToNext());
    }

    @Test
    public void cannotMovePastLastLine() throws Exception {
        IFileReader reader = FileReader.read(getFile("CsvFileWithInvalidEntries"));

        int lineCount = reader.getLineCount();
        moveReaderXTimes(reader, lineCount);

        assertFalse(reader.moveToNext());
    }

    private void moveReaderXTimes(IFileReader reader, int times) {
        for (int i = 0; i < times; i++) {
            reader.moveToNext();
        }
    }

    private IFile getFile(String fileName) {
        return new File(String.format(
                "%s/%s.csv",
                TEST_FILE_DIR,
                fileName
        ));
    }
}
