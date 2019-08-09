package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;

public class CSVFileTest {
    private final String fileWithHeadline = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/Files/CsvFiles/importableExpenseObjects.csv";
    private final String fileWithoutHeadline = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/Files/CsvFiles/importableExpensesWithoutHeaderLine.csv";

    @Test
    public void throwsExceptionIfFileIsNotCsv() throws FileNotFoundException {
        try {
            CSVFile.open("file/with/invalid/extension.txt");

            Assert.fail("FileReader with invalid extension could be opened");
        } catch (InvalidFileException e) {

            assertEquals("Could not open file: file/with/invalid/extension.txt. Expected file of type .csv.", e.getMessage());
        }
    }

    @Test
    public void canReadFileWithHeader() throws Exception {
        CSVFile file = CSVFile.open(fileWithHeadline);

        String expectedHeader = "title,amount,date,category,notice,account";
        String actualHeader = file.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void canReadFileWithoutHeader() throws Exception {
        CSVFile file = CSVFile.open(fileWithoutHeadline);

        String expectedHeader = "Meine Geile Buchung,100.00,09-08-2019 08:00:00,Essen,,Sparschwein";
        String actualHeader = file.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }
}
