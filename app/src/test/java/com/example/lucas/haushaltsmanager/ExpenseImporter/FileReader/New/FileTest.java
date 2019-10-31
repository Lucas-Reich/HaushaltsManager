package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.New;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.File;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.IFile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileTest {
    private final String BASE_PATH = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/TextFiles";
    private final String FILE_NAME = "emptyFile";
    private final String FILE_TYPE = "txt";

    @Test
    public void canExtractFileType() {
        // SetUp
        IFile file = new File(getTestFile());


        // Act
        String actualFileType = file.getType();


        // Assert
        assertEquals(FILE_TYPE, actualFileType);
    }

    @Test
    public void returnsCompletePath() {
        IFile file = new File(getTestFile());

        assertEquals(getTestFile(), file.getPath());
    }

    @Test
    public void cannotCreateFileFromNoFileReference() {
        try {
            new File("invalid/file/path");
        } catch (InvalidFileException e) {
            assertEquals("Given path 'invalid/file/path' does not reference a File.", e.getMessage());
        }


    }

    private String getTestFile() {
        return String.format("%s/%s.%s",
                BASE_PATH,
                FILE_NAME,
                FILE_TYPE
        );
    }
}
