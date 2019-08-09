package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String requiredExtension, String path) {
        super(String.format("Could not open file: %s. Expected file of type %s.",
                path,
                requiredExtension
        ));
    }
}
