package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader;

import static junit.framework.TestCase.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.CSVFile;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class CSVFileTest {
    private final String fileWithHeadline = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/CsvFiles/importableExpenseObjects.csv";
    private final String fileWithoutHeadline = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/CsvFiles/importableExpensesWithoutHeaderLine.csv";
    private final String emptyFile = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/CsvFiles/emptyFile.csv";

    @Test
    public void throwsExceptionIfFileIsNotCsv() throws FileNotFoundException {
        try {
            CSVFile.open("file/with/invalid/extension.txt");

            Assert.fail("CSVFileReader with invalid extension could be opened");
        } catch (InvalidFileException e) {

            assertEquals("Could not open file: file/with/invalid/extension.txt. Expected file of type .csv.", e.getMessage());
        }
    }

    @Test
    public void canReadFileWithHeader() throws FileNotFoundException {
        CSVFile file = CSVFile.open(fileWithHeadline);

        String expectedHeader = "title,amount,date,category,account";
        String actualHeader = file.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void headerIsCorrectlySplit() throws FileNotFoundException {
        CSVFile file = CSVFile.open(fileWithHeadline);

        String[] actualHeader = file.getHeaders();

        assertEquals("title", actualHeader[0]);
        assertEquals("amount", actualHeader[1]);
        assertEquals("date", actualHeader[2]);
        assertEquals("category", actualHeader[3]);
        assertEquals("account", actualHeader[4]);
    }

    @Test
    public void canReadFileWithoutHeader() throws FileNotFoundException {
        CSVFile file = CSVFile.open(fileWithoutHeadline);

        String expectedHeader = "Meine Geile Buchung,100.00,09-08-2019 08:00:00,Essen,Sparschwein";
        String actualHeader = file.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void returnsEmptyStringIfFileIsEmpty() throws FileNotFoundException {
        CSVFile file = CSVFile.open(emptyFile);

        String emptyLine = file.getHeader();

        assertEquals("", emptyLine);
    }
}
