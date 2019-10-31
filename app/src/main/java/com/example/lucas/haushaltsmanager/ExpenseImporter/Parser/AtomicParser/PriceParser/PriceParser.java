package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Type;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.Arrays;
import java.util.List;

public class PriceParser implements IParser<Price> {
    public static final IRequiredField PRICE_VALUE_KEY = new Value();
    public static final IRequiredField PRICE_TYPE_KEY = new Type();

    private BooleanParser booleanParser;
    private DoubleParser doubleParser;
    private final Currency mainCurrency;

    public PriceParser(Currency currency) {
        this.mainCurrency = currency;

        booleanParser = new BooleanParser();
        doubleParser = new DoubleParser();
    }

    @Override
    public List<IRequiredField> getRequiredFields() {
        return Arrays.asList(
                PRICE_VALUE_KEY,
                PRICE_TYPE_KEY
        );
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
