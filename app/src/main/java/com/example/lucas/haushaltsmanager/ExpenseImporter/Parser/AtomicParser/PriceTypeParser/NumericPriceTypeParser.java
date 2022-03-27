package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class NumericPriceTypeParser implements IParser<Boolean> {
    public static final IRequiredField PRICE_TYPE_KEY = new PriceType();

    public static final boolean VALUE_POSITIVE = false;
    public static final boolean VALUE_NEGATIVE = true;

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return Collections.singletonList(PRICE_TYPE_KEY);
    }

    @Override
    @NonNull
    public Boolean parse(@NonNull Line line, @NonNull MappingList mappings) throws NoMappingFoundException, InvalidInputException {
        String value = line.getAsString(mappings.getMappingForKey(PRICE_TYPE_KEY));

        if (!isNumeric2(value)) {
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

    private boolean isNumeric2(String input) {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(symbols);

        try {
            df.parse(input);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}