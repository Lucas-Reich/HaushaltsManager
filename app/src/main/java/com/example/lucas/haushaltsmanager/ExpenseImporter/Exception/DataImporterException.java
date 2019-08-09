package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public abstract class DataImporterException extends RuntimeException {
    DataImporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
