package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.Collections;
import java.util.List;

public class PriceParser implements IParser<Price> {
    public static final IRequiredField PRICE_VALUE_KEY = new Value();

    private final DoubleParser doubleParser;

    public PriceParser() {
        doubleParser = new DoubleParser();
    }

    @Override
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(
                PRICE_VALUE_KEY
        );
    }

    public Price parse(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        String valueString = line.getAsString(mapping.getMappingForKey(PRICE_VALUE_KEY));
        assertNotEmpty(valueString);

        return new Price(parseValue(valueString));
    }

    private double parseValue(String input) throws InvalidInputException {
        try {
            double value = doubleParser.parse(input);

            return Math.abs(value);
        } catch (NumberFormatException e) {

            throw InvalidInputException.invalidPriceValue(input, e);
        }
    }

    private void assertNotEmpty(String string) throws InvalidInputException {
        if (!string.isEmpty()) {
            return;
        }

        throw InvalidInputException.emptyInput(Price.class);
    }
}
