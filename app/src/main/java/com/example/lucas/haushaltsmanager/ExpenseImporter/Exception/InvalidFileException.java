package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

import java.io.File;

public class InvalidFileException extends RuntimeException {
    private InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidFileException invalidType(String requiredExtension, String path, String givenFileType) {
        return new InvalidFileException(String.format(
                "Could not open file: '%s'. Expected file of type '%s' but got '%s'.",
                path,
                requiredExtension,
                givenFileType
        ), null);
    }

    public static InvalidFileException notAFile(String filePath) {
        return new InvalidFileException(String.format(
                "Given path '%s' does not reference a File.",
                filePath
        ), null);
    }

    public static InvalidFileException generic(File file) {
        return new InvalidFileException(String.format(
                "Something went wrong while handling file: %s.",
                file.getName()
        ), null);
    }

    public static InvalidFileException nullGiven() {
        return new InvalidFileException("Expected type File got null.", null);
    }
}
