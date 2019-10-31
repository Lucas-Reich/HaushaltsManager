package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

public class NoMappingFoundException extends DataImporterException {
    private NoMappingFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NoMappingFoundException withRequiredField(IRequiredField key) {
        return new NoMappingFoundException(String.format("No mapping defined for key '%s'.",
                key.getClass().getSimpleName()
        ), null);
    }
}
