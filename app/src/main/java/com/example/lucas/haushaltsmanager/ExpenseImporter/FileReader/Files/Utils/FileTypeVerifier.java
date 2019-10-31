package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils;

public class FileTypeVerifier {
    private final String expectedExtension;

    public FileTypeVerifier(String requiredExtension) {
        expectedExtension = requiredExtension;
    }

    public boolean verifyType(String filePath) {
        String actualFileExtension = filePath.substring(filePath.indexOf("."));

        return actualFileExtension.equals(expectedExtension);
    }
}
