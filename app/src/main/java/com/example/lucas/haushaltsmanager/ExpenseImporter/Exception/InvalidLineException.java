package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public class InvalidLineException extends DataImporterException {
    private InvalidLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidLineException withInvalidEntryCount(int expectedArgumentCount, int actualArgumentCount) {
        return new InvalidLineException(String.format("Could not import malformed line. Expected %s arguments, got %s arguments.",
                expectedArgumentCount,
                actualArgumentCount
        ), null);
    }
}
