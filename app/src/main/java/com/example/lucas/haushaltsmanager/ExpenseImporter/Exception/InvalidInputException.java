package com.example.lucas.haushaltsmanager.ExpenseImporter.Exception;

import com.example.lucas.haushaltsmanager.entities.Price;

public class InvalidInputException extends DataImporterException {
    private InvalidInputException(String object, String argument, String exceptionType, Throwable cause) {
        super(String.format("Could not create %s from '%s', invalid %s.", object, argument, exceptionType), cause);
    }

    public static InvalidInputException invalidDateFormat(String invalidDate) {
        return new InvalidInputException(
                "Date",
                invalidDate,
                "format",
                null
        );
    }

    public static InvalidInputException emptyInput(Class clazz) {
        return new InvalidInputException(
                clazz.getSimpleName(),
                "empty string",
                "input",
                null
        );
    }

    public static InvalidInputException invalidPriceValue(String invalidValue, Throwable t) {
        return new InvalidInputException(
                Price.class.getSimpleName(),
                invalidValue,
                "value",
                t
        );
    }

    public static InvalidInputException invalidPriceType(String invalidType, Throwable t) {
        return new InvalidInputException(
                Price.class.getSimpleName(),
                invalidType,
                "type",
                t
        );
    }
}
