package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

public class ExtensionVerifier {
    private final String expectedExtension;

    public ExtensionVerifier(String requiredExtension) {
        expectedExtension = requiredExtension;
    }

    public boolean verifyExtension(String filePath) {
        String actualFileExtension = filePath.substring(filePath.indexOf("."));

        return actualFileExtension.equals(expectedExtension);
    }
}
