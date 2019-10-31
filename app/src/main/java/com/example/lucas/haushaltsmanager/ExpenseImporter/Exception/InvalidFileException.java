package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public class InvalidFileException extends RuntimeException {
    private InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidFileException invalidType(String requiredExtension, String path) {
        return new InvalidFileException(String.format(
                "Could not open file: %s. Expected file of type %s.",
                path,
                requiredExtension
        ), null);
    }

    public static InvalidFileException notAFile(String filePath) {
        return new InvalidFileException(String.format(
                "Given path '%s' does not reference a File.",
                filePath
        ), null);
    }
}
