package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils;

import com.example.lucas.haushaltsmanager.Utils.FileUtils;

public class FileTypeVerifier {
    private final String expectedExtension;

    public FileTypeVerifier(String requiredExtension) {
        expectedExtension = requiredExtension;
    }

    public boolean verifyType(String filePath) {
        String actualFileExtension = FileUtils.getType(filePath);

        return actualFileExtension.equals(expectedExtension);
    }
}
