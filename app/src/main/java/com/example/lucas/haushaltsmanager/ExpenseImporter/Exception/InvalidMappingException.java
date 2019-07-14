package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

public class InvalidMappingException extends RuntimeException {
    private InvalidMappingException(String message, Throwable previous) {
        super(message, previous);
    }

    public static InvalidMappingException tooFewFields(int expectedAmount, int givenAmount) {
        return new InvalidMappingException(
                String.format("Line does not have enough fields to map. Given: %s, expected: %s.", givenAmount, expectedAmount),
                null
        );
    }

    public static InvalidMappingException noValueFoundForIndex(int index) {
        return new InvalidMappingException(
                String.format("Expected value at index: %s, but found none.", index),
                null
        );
    }
}
