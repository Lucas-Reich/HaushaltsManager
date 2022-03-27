package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.List;

public class PriceParser implements IParser<Price> {
    private final IParser<Double> valueParser;
    private final IParser<Boolean> priceTypeParser;

    public PriceParser(IParser<Double> valueParser, IParser<Boolean> priceTypeParse) {
        this.valueParser = valueParser;
        this.priceTypeParser = priceTypeParse;
    }

    @Override
    @NonNull
    public List<IRequiredField> getRequiredFields() {
        return new ArrayList<IRequiredField>() {{
            addAll(valueParser.getRequiredFields());
            addAll(priceTypeParser.getRequiredFields());
        }};
    }

    @Override
    @NonNull
    public Price parse(@NonNull Line line, @NonNull MappingList mapping) throws NoMappingFoundException, InvalidInputException {
        boolean priceType = this.priceTypeParser.parse(line, mapping);

        if (priceType == NumericPriceTypeParser.VALUE_NEGATIVE) {
            return new Price(
                    valueParser.parse(line, mapping),
                    NumericPriceTypeParser.VALUE_NEGATIVE
            );
        }

        return new Price(
                valueParser.parse(line, mapping),
                NumericPriceTypeParser.VALUE_POSITIVE
        );
    }
}
