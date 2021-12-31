package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BooleanPriceTypeParser implements IPriceTypeParser {
    private final List<String> VALID_BOOLEAN_TRUE = Arrays.asList("true", "1");
    private final List<String> VALID_BOOLEAN_FALSE = Arrays.asList("false", "0");

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(IPriceTypeParser.Companion.getPRICE_TYPE_KEY());
    }

    @Override
    @NonNull
    public Boolean parse(@NonNull Line line, @NonNull MappingList mappings) throws NoMappingFoundException, InvalidInputException {
        String typeString = line.getAsString(mappings.getMappingForKey(IPriceTypeParser.Companion.getPRICE_TYPE_KEY()));
        typeString = typeString.toLowerCase();

        if (VALID_BOOLEAN_TRUE.contains(typeString)) {
            return VALUE_NEGATIVE;
        }

        if (VALID_BOOLEAN_FALSE.contains(typeString)) {
            return VALUE_POSITIVE;
        }

        throw InvalidInputException.invalidBooleanValue(typeString, null);
    }
}
