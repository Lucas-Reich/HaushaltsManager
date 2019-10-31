package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

abstract class DataImporterException extends RuntimeException {
    DataImporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
