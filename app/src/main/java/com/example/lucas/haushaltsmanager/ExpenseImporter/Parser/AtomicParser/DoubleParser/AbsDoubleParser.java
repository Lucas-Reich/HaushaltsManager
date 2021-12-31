package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.Collections;
import java.util.List;

public class AbsDoubleParser implements IParser<Double> {
    public static final IRequiredField PRICE_VALUE_KEY = new PriceValue();

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(PRICE_VALUE_KEY);
    }

    @Override
    @NonNull
    public Double parse(@NonNull Line line, @NonNull MappingList mappings) throws NoMappingFoundException, InvalidInputException {
        String valueString = line.getAsString(mappings.getMappingForKey(PRICE_VALUE_KEY));

        return parseDoubleValue(valueString);
    }

    private double parseDoubleValue(@NonNull String input) throws InvalidInputException {
        try {
            return Math.abs(Double.parseDouble(input));
        } catch (NumberFormatException e) {
            throw InvalidInputException.invalidNumericValue(input, e);
        }
    }
}
