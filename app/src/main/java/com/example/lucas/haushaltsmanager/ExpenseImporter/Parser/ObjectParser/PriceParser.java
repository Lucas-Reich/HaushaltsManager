package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.BooleanParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.DoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

public class PriceParser implements IObjectParser<Price> {
    public static final String PRICE_VALUE_KEY = "price_value";
    public static final String PRICE_TYPE_KEY = "price_type";

    private BooleanParser booleanParser;
    private DoubleParser doubleParser;
    private final Currency mainCurrency;

    public PriceParser(Currency currency) {
        this.mainCurrency = currency;

        booleanParser = new BooleanParser();
        doubleParser = new DoubleParser();
    }

    public Price parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String valueString = line.getAsString(mapping.getMappingForKey(PRICE_VALUE_KEY));
        assertNotEmpty(valueString);

        String typeString = line.getAsString(mapping.getMappingForKey(PRICE_TYPE_KEY));
        assertNotEmpty(typeString);

        return new Price(
                parseValue(valueString),
                parseType(typeString),
                mainCurrency
        );
    }

    private double parseValue(String input) throws InvalidInputException {
        try {
            double value = doubleParser.parse(input);

            return Math.abs(value);
        } catch (NumberFormatException e) {

            throw InvalidInputException.invalidPriceValue(input, e);
        }
    }

    private boolean parseType(String input) throws InvalidInputException {
        try {

            return booleanParser.parse(input);
        } catch (IllegalArgumentException e) {

            try {
                double value = doubleParser.parse(input);

                return value < 0;
            } catch (NumberFormatException t) {

                throw InvalidInputException.invalidPriceType(input, t);
            }
        }
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Price.class);
    }
}
