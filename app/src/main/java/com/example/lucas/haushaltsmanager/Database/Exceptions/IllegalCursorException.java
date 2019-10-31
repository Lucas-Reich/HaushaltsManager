package com.example.lucas.haushaltsmanager.Database.Exceptions;

public class IllegalCursorException extends IllegalArgumentException {
    IllegalCursorException(String message, Throwable previous) {
        super(message, previous);
    }

    public static IllegalCursorException nullCursor(String entity, Throwable previous) {
        return new IllegalCursorException(
                String.format("Could not create class '%s', given Cursor was NULL.", entity),
                previous
        );
    }

    public static IllegalCursorException missingField(String fieldName, Throwable previous) {
        return new IllegalCursorException(
                String.format("Could not find required field '%s' in Cursor.", fieldName),
                previous
        );
    }
}
