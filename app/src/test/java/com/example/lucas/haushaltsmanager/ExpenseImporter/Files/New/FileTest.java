package com.example.lucas.haushaltsmanager.ExpenseImporter.Files.New;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.File;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.IFile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileTest {
    @Test
    public void canExtractFileExtension() {
        // SetUp
        String expectedExtension = "any";
        IFile file = new File(String.format("fileName.%s", expectedExtension));


        // Act
        String actualExtension = file.getExtension();


        // Assert
        assertEquals(expectedExtension, actualExtension);
    }

    @Test
    public void returnsCompletePath() {
        String expectedPath = "path/to/file.any";

        IFile file = new File(expectedPath);

        assertEquals(expectedPath, file.getPath());
    }
}
