package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

public class NoMappingFoundException extends DataImporterException {
    private NoMappingFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @NonNull
    public static NoMappingFoundException forRequiredField(@NonNull IRequiredField key) {
        return new NoMappingFoundException(String.format(
                "No mapping defined for key '%s'.",
                key.getClass().getSimpleName()
        ), null);
    }
}
