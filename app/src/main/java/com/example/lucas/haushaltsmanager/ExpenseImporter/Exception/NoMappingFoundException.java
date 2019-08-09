package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public class NoMappingFoundException extends DataImporterException {
    private NoMappingFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NoMappingFoundException withKey(String key) {
        return new NoMappingFoundException(String.format("No mapping defined for key '%s'.",
                key
        ), null);
    }
}
