package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import java.util.Arrays;
import java.util.List;

class BooleanParser {
    private final List<String> VALID_BOOLEAN_TRUE = Arrays.asList("1", "true");
    private final List<String> VALID_BOOLEAN_FALSE = Arrays.asList("0", "false");

    Boolean parse(String input) throws IllegalArgumentException {
        input = input.toLowerCase();

        if (VALID_BOOLEAN_TRUE.contains(input)) {
            return true;
        }

        if (VALID_BOOLEAN_FALSE.contains(input)) {
            return false;
        }

        throw new IllegalArgumentException(String.format("Could not parse '%s' to Boolean.", input));
    }
}
