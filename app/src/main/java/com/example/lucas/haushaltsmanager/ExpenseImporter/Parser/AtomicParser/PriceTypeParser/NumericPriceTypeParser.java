package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.Collections;
import java.util.List;

public class NumericPriceTypeParser implements IPriceTypeParser {
    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(IPriceTypeParser.Companion.getPRICE_TYPE_KEY());
    }

    @Override
    @NonNull
    public Boolean parse(@NonNull Line line, @NonNull MappingList mappings) throws NoMappingFoundException, InvalidInputException {
        String value = line.getAsString(mappings.getMappingForKey(IPriceTypeParser.Companion.getPRICE_TYPE_KEY()));

        if (!isNumeric(value)) {
            throw InvalidInputException.invalidNumericValue(value, null);
        }

        return parseValue(value);
    }

    private boolean parseValue(String input) {
        return input.contains("-") ? VALUE_NEGATIVE : VALUE_POSITIVE;
    }

    private boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}