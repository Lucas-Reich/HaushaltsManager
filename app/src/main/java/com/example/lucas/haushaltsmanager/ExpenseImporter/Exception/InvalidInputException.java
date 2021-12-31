package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InvalidInputException extends DataImporterException {
    private InvalidInputException(String object, String invalidInput, String exceptionType, Throwable cause) {
        super(String.format("Could not create '%s' from '%s', invalid %s.", object, invalidInput, exceptionType), cause);
    }

    public static InvalidInputException invalidDateFormat(@NonNull String invalidDate) {
        return new InvalidInputException(
                "Date",
                invalidDate,
                "format",
                null
        );
    }

    public static InvalidInputException emptyInput(@NonNull Class clazz) {
        return new InvalidInputException(
                clazz.getSimpleName(),
                "empty string",
                "input",
                null
        );
    }

    public static InvalidInputException invalidBooleanValue(@NonNull String invalidType, @Nullable Throwable t) {
        return new InvalidInputException(
                Boolean.class.getSimpleName(),
                invalidType,
                "input",
                t
        );
    }

    public static InvalidInputException invalidNumericValue(@NonNull String invalidType, @Nullable Throwable t) {
        return new InvalidInputException(
                Boolean.class.getSimpleName(),
                invalidType,
                "numeric value given",
                t
        );
    }
}
